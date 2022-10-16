package top.e404.edropper.exception

class NoSuchSchemGroupException(
    name: String
) : Exception("cannot find schem group with name($name)")