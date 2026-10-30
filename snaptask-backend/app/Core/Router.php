<?php
declare(strict_types=1);

namespace App\Core;

/**
 * Fast REST Router with Middleware and Route Parameters
 */
class Router
{
    private array $routes = [];
    private string $currentPrefix = '';
    private array $currentGroupMiddleware = [];

    public function group(string $prefix, array $middleware, callable $callback): void
    {
        $previousPrefix = $this->currentPrefix;
        $previousMiddleware = $this->currentGroupMiddleware;

        $this->currentPrefix = rtrim($previousPrefix . '/' . trim($prefix, '/'), '/');
        $this->currentGroupMiddleware = array_merge($previousMiddleware, $middleware);

        $callback($this);

        $this->currentPrefix = $previousPrefix;
        $this->currentGroupMiddleware = $previousMiddleware;
    }

    public function addRoute(string $method, string $path, mixed $handler, array $middleware = []): void
    {
        $fullPath = rtrim($this->currentPrefix . '/' . trim($path, '/'), '/');
        if ($fullPath === '') {
            $fullPath = '/';
        }

        $allMiddleware = array_merge($this->currentGroupMiddleware, $middleware);

        $this->routes[] = [
            'method' => strtoupper($method),
            'path' => $fullPath,
            'handler' => $handler,
            'middleware' => $allMiddleware,
            'regex' => $this->compilePattern($fullPath),
        ];
    }

    public function get(string $path, mixed $handler, array $middleware = []): void
    {
        $this->addRoute('GET', $path, $handler, $middleware);
    }

    public function post(string $path, mixed $handler, array $middleware = []): void
    {
        $this->addRoute('POST', $path, $handler, $middleware);
    }

    public function put(string $path, mixed $handler, array $middleware = []): void
    {
        $this->addRoute('PUT', $path, $handler, $middleware);
    }

    public function delete(string $path, mixed $handler, array $middleware = []): void
    {
        $this->addRoute('DELETE', $path, $handler, $middleware);
    }

    public function options(string $path, mixed $handler, array $middleware = []): void
    {
        $this->addRoute('OPTIONS', $path, $handler, $middleware);
    }

    private function compilePattern(string $path): string
    {
        $pattern = preg_replace('/\{([a-zA-Z0-9_]+)\}/', '(?P<$1>[^/]+)', $path);
        return '#^' . $pattern . '$#';
    }

    public function dispatch(Request $request): void
    {
        $requestMethod = $request->getMethod();
        $requestPath = $request->getPath();

        // Automatic OPTIONS preflight
        if ($requestMethod === 'OPTIONS') {
            Response::json(['preflight' => true], 'Preflight OK', 200, [], [
                'Access-Control-Allow-Origin' => '*',
                'Access-Control-Allow-Methods' => 'GET, POST, PUT, DELETE, OPTIONS, PATCH',
                'Access-Control-Allow-Headers' => 'Authorization, Content-Type, Accept, X-Requested-With',
            ]);
            return;
        }

        $matchedRoute = null;
        $allowedMethods = [];

        foreach ($this->routes as $route) {
            if (preg_match($route['regex'], $requestPath, $matches)) {
                $allowedMethods[] = $route['method'];
                if ($route['method'] === $requestMethod) {
                    $matchedRoute = $route;
                    $routeParams = [];
                    foreach ($matches as $k => $v) {
                        if (!is_int($k)) {
                            $routeParams[$k] = $v;
                        }
                    }
                    $request->setRouteParams($routeParams);
                    break;
                }
            }
        }

        if ($matchedRoute === null) {
            if (!empty($allowedMethods)) {
                Response::error("Method {$requestMethod} not allowed for {$requestPath}", 405, [
                    'allowed_methods' => array_unique($allowedMethods),
                ], 'METHOD_NOT_ALLOWED');
                return;
            }
            Response::notFound("Route not found: {$requestMethod} {$requestPath}");
            return;
        }

        // Execute Middleware Chain
        foreach ($matchedRoute['middleware'] as $mw) {
            $middlewareInstance = $this->resolveMiddleware($mw);
            $middlewareInstance->handle($request);
        }

        // Execute Controller / Handler
        $this->executeHandler($matchedRoute['handler'], $request);
    }

    private function resolveMiddleware(mixed $mw): object
    {
        if (is_string($mw)) {
            $class = match ($mw) {
                'auth' => \App\Middleware\AuthMiddleware::class,
                'cors' => \App\Middleware\CorsMiddleware::class,
                'ratelimit' => \App\Middleware\RateLimitMiddleware::class,
                default => $mw,
            };
            return new $class();
        }
        return $mw;
    }

    private function executeHandler(mixed $handler, Request $request): void
    {
        if (is_callable($handler)) {
            $handler($request);
            return;
        }

        if (is_array($handler) && count($handler) === 2) {
            [$class, $method] = $handler;
            $controller = new $class();
            $controller->$method($request);
            return;
        }

        throw new \RuntimeException('Invalid route handler configuration');
    }
}

