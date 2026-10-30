<?php
declare(strict_types=1);

namespace Tests\Unit;

use App\Helpers\Validator;
use PHPUnit\Framework\TestCase;

class ValidatorTest extends TestCase
{
    public function testRequiredRulePassesWhenPresent(): void
    {
        $v = Validator::make(['email' => 'test@example.com'], ['email' => 'required']);
        $this->assertFalse($v->fails());
    }

    public function testRequiredRuleFailsWhenMissing(): void
    {
        $v = Validator::make([], ['email' => 'required']);
        $this->assertTrue($v->fails());
        $this->assertArrayHasKey('email', $v->errors());
    }

    public function testEmailRuleValidatesProperEmail(): void
    {
        $v = Validator::make(['email' => 'invalid-email'], ['email' => 'email']);
        $this->assertTrue($v->fails());

        $v2 = Validator::make(['email' => 'alex@snaptask.app'], ['email' => 'email']);
        $this->assertFalse($v2->fails());
    }

    public function testMinMaxRules(): void
    {
        $v = Validator::make(['name' => 'A'], ['name' => 'min:2|max:10']);
        $this->assertTrue($v->fails());

        $v2 = Validator::make(['name' => 'Valid Name'], ['name' => 'min:2|max:20']);
        $this->assertFalse($v2->fails());
    }

    public function testInRule(): void
    {
        $v = Validator::make(['category' => 'invalid'], ['category' => 'in:dated,non_dated,unclassified']);
        $this->assertTrue($v->fails());

        $v2 = Validator::make(['category' => 'dated'], ['category' => 'in:dated,non_dated,unclassified']);
        $this->assertFalse($v2->fails());
    }

    public function testDateRule(): void
    {
        $v = Validator::make(['date' => '2026-10-16'], ['date' => 'date']);
        $this->assertFalse($v->fails());

        $v2 = Validator::make(['date' => 'not-a-date'], ['date' => 'date']);
        $this->assertTrue($v2->fails());
    }
}

