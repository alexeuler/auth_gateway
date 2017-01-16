package services

import java.security.MessageDigest

import com.google.common.io.BaseEncoding

object Encryption {
  def md5(s: String): String = {
    val bytes = MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8"))
    BaseEncoding.base64().encode(bytes)
  }
}
