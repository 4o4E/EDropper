package top.e404.edropper.exception

class NoSuchSchemException(
    name: String
) : Exception("cannot find schem file with name($name)")