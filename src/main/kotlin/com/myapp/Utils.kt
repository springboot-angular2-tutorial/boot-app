package com.myapp

import java.security.MessageDigest
import javax.xml.bind.DatatypeConverter

fun String.md5(): String {
    val digester = MessageDigest.getInstance("MD5")
    val bytes = digester.digest(toByteArray())
    return DatatypeConverter.printHexBinary(bytes).toLowerCase()
}
