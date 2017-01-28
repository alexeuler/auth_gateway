package services

import java.security.{MessageDigest, SecureRandom}

import com.google.common.io.BaseEncoding

object Encryption {
  def md5(s: String): String = {
    val bytes = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))
    BaseEncoding.base64().encode(bytes)
  }

  def randomHash(): String = {
    val seedSize = 55
    val randomBytesSize = 32
    val random = new SecureRandom()
    random.setSeed(random.generateSeed(seedSize))
    val bytes = new Array[Byte](randomBytesSize)
    random.nextBytes(bytes)
    BaseEncoding.base64().encode(bytes)
  }
}
