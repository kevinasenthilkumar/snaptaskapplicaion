<?php
declare(strict_types=1);

namespace App\Models;

class User
{
    public int $id;
    public string $email;
    public string $password_hash;
    public string $name;
    public ?string $avatar_url = null;
    public string $status = 'active';
    public ?string $created_at = null;
    public ?string $updated_at = null;

    public function toArray(): array
    {
        return [
            'id' => (int)$this->id,
            'email' => $this->email,
            'name' => $this->name,
            'avatar_url' => $this->avatar_url,
            'status' => $this->status,
            'created_at' => $this->created_at,
            'updated_at' => $this->updated_at,
        ];
    }
}

