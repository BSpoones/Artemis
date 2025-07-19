package org.beespoon.artemis.command.argument

enum class ParseFailReason {
    DATA_NOT_FOUND,
    INVALID_TYPE,
    INVALID_RANGE,
    INVALID_FORMAT,
    INVALID_VALUE,
    PERMISSION_DENIED,
    RATE_LIMIT_EXCEEDED,
    UNEXPECTED_VALUE;
}