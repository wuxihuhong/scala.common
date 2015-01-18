package huhong.scala.common.lucene.analysis

import java.io.Reader

import org.apache.lucene.analysis.tokenattributes.{OffsetAttribute, CharTermAttribute}
import org.apache.lucene.analysis.util.CharTokenizer
import org.apache.lucene.analysis.{TokenStream, Tokenizer}
import org.apache.lucene.util.{Version, AttributeFactory}

import scala.util.control.Breaks._

/**
 * Created by huhong on 15/1/17.
 */
class CharSplitTokenizer(factory: AttributeFactory, in: Reader) extends Tokenizer(factory, in) {

  def this(in: Reader) = this(TokenStream.DEFAULT_TOKEN_ATTRIBUTE_FACTORY, in)

  private var offset = 0
  private var bufferIndex = 0
  private var dataLen = 0
  private val buffer = new Array[Char](CharSplitTokenizer.MAX_WORD_LEN)
  private val ioBuffer = new Array[Char](CharSplitTokenizer.IO_BUFFER_SIZE)

  private var length = 0
  private var start = 0

  private val termAtt = addAttribute(classOf[CharTermAttribute])
  private val offsetAtt = addAttribute(classOf[OffsetAttribute])


  def push(c: Char) = {
    if (length == 0) start = offset - 1
    buffer(length) = c //Character.toLowerCase(c)
    length += 1
  }


  def flush() = {
    if (length > 0) {
      termAtt.copyBuffer(buffer, 0, length)
      offsetAtt.setOffset(correctOffset(start), correctOffset(start + length))
      true
    } else {
      false
    }
  }

  override def incrementToken(): Boolean = {
    clearAttributes()
    length = 0
    start = offset

    breakable {
      while (true) {

        var c = ' '
        offset += 1
        if (bufferIndex >= dataLen) {
          dataLen = input.read(ioBuffer)
          bufferIndex = 0
        }
        if (dataLen == -1) {
          offset -= 1
          break()
        } else {
          c = ioBuffer(bufferIndex)
          bufferIndex += 1
        }


        if (length > 0) {
          bufferIndex -= 1
          offset -= 1
          break()
        } else {
          push(c)
          break()
        }
        //        Character.getType(c) match {
        //          case Character.DECIMAL_DIGIT_NUMBER | Character.LOWERCASE_LETTER | Character.UPPERCASE_LETTER => {println(c)}
        //          case Character.OTHER_LETTER => {
        //            if (length > 0) {
        //              bufferIndex -= 1
        //              offset -= 1
        //              break()
        //            } else {
        //              push(c)
        //              break()
        //            }
        //          }
        //          case _ => {
        //            if (length > 0) break()
        //          }
        //        }
      }
    }

    flush()
  }

  override def end(): Unit = {
    val finalOffset = correctOffset(offset);
    offsetAtt.setOffset(finalOffset, finalOffset);
  }

  override def reset(): Unit = {
    super.reset()
    offset = 0
    bufferIndex = 0
    dataLen = 0
  }

}

object CharSplitTokenizer {
  private val MAX_WORD_LEN = 255
  private val IO_BUFFER_SIZE = 1024
}
