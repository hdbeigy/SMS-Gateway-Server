package ir.hdb.sms_server.apis.exceptions

class ConnectionFailedException : Exception {
    constructor() {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}
}