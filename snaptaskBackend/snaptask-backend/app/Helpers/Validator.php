<?php
declare(strict_types=1);

namespace App\Helpers;

/**
 * Clean, expressive Data Validation Engine
 */
class Validator
{
    private array $data;
    private array $rules;
    private array $errors = [];

    public function __construct(array $data, array $rules)
    {
        $this->data = $data;
        $this->rules = $rules;
    }

    public static function make(array $data, array $rules): self
    {
        $validator = new self($data, $rules);
        $validator->validate();
        return $validator;
    }

    public function fails(): bool
    {
        return !empty($this->errors);
    }

    public function errors(): array
    {
        return $this->errors;
    }

    public function validated(): array
    {
        $keys = array_keys($this->rules);
        $validated = [];
        foreach ($keys as $key) {
            if (array_key_exists($key, $this->data)) {
                $validated[$key] = $this->data[$key];
            }
        }
        return $validated;
    }

    private function validate(): void
    {
        foreach ($this->rules as $field => $ruleSet) {
            $rules = is_array($ruleSet) ? $ruleSet : explode('|', $ruleSet);
            $value = $this->data[$field] ?? null;

            foreach ($rules as $rule) {
                $param = null;
                if (str_contains($rule, ':')) {
                    [$rule, $param] = explode(':', $rule, 2);
                }

                $this->applyRule($field, $value, $rule, $param);
            }
        }
    }

    private function applyRule(string $field, mixed $value, string $rule, ?string $param): void
    {
        switch ($rule) {
            case 'required':
                if ($value === null || $value === '' || (is_array($value) && empty($value))) {
                    $this->addError($field, "The {$field} field is required.");
                }
                break;

            case 'email':
                if ($value !== null && $value !== '' && !filter_var($value, FILTER_VALIDATE_EMAIL)) {
                    $this->addError($field, "The {$field} must be a valid email address.");
                }
                break;

            case 'min':
                $min = (int)$param;
                if (is_string($value) && mb_strlen($value) < $min) {
                    $this->addError($field, "The {$field} must be at least {$min} characters.");
                } elseif (is_numeric($value) && $value < $min) {
                    $this->addError($field, "The {$field} must be at least {$min}.");
                } elseif (is_array($value) && count($value) < $min) {
                    $this->addError($field, "The {$field} must have at least {$min} items.");
                }
                break;

            case 'max':
                $max = (int)$param;
                if (is_string($value) && mb_strlen($value) > $max) {
                    $this->addError($field, "The {$field} must not exceed {$max} characters.");
                } elseif (is_numeric($value) && $value > $max) {
                    $this->addError($field, "The {$field} must not exceed {$max}.");
                } elseif (is_array($value) && count($value) > $max) {
                    $this->addError($field, "The {$field} must not have more than {$max} items.");
                }
                break;

            case 'in':
                $allowed = explode(',', (string)$param);
                if ($value !== null && $value !== '' && !in_array((string)$value, $allowed, true)) {
                    $this->addError($field, "The {$field} must be one of: " . implode(', ', $allowed));
                }
                break;

            case 'numeric':
                if ($value !== null && $value !== '' && !is_numeric($value)) {
                    $this->addError($field, "The {$field} must be a number.");
                }
                break;

            case 'boolean':
                if ($value !== null && $value !== '' && !is_bool($value) && !in_array($value, [0, 1, '0', '1', 'true', 'false'], true)) {
                    $this->addError($field, "The {$field} must be true or false.");
                }
                break;

            case 'date':
                if ($value !== null && $value !== '') {
                    $d = \DateTime::createFromFormat('Y-m-d', (string)$value);
                    if (!$d || $d->format('Y-m-d') !== (string)$value) {
                        $this->addError($field, "The {$field} must be a valid date in YYYY-MM-DD format.");
                    }
                }
                break;

            case 'time':
                if ($value !== null && $value !== '') {
                    if (!preg_match('/^(?:2[0-3]|[01][0-9]):[0-5][0-9](?::[0-5][0-9])?$/', (string)$value)) {
                        $this->addError($field, "The {$field} must be a valid time format (HH:MM or HH:MM:SS).");
                    }
                }
                break;

            case 'array':
                if ($value !== null && !is_array($value)) {
                    $this->addError($field, "The {$field} must be an array.");
                }
                break;
        }
    }

    private function addError(string $field, string $message): void
    {
        $this->errors[$field][] = $message;
    }
}

