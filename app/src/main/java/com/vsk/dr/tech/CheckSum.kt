package com.vsk.dr.tech

import java.io.FileInputStream
import java.security.DigestInputStream
import java.security.MessageDigest

object CheckSum {
    @Throws(Exception::class)
    fun generateSHA256(filePath: String): String {
        var digest = MessageDigest.getInstance("SHA-256")

        FileInputStream(filePath).use { fis ->
            DigestInputStream(fis, digest).use { dis ->
                val buffer = ByteArray(8192)
                while (dis.read(buffer) != -1) {
                    // Read file in chunks and update digest
                }
                digest = dis.messageDigest
            }
        }
        val hash = digest.digest()

        val hexString = StringBuilder()

        for (b in hash) {
            val hex = Integer.toHexString(0xff and b.toInt())

            if (hex.length == 1) hexString.append('0')

            hexString.append(hex)
        }

        return hexString.toString()
    }
}