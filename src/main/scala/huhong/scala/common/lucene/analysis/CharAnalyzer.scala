package huhong.scala.common.lucene.analysis

import java.io.Reader

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

/**
 * Created by huhong on 15/1/18.
 */
class CharAnalyzer extends Analyzer {
  def createComponents(fieldName: String, reader: Reader): TokenStreamComponents = {
    val token = new CharSplitTokenizer(reader)
    new TokenStreamComponents(token)

  }
}
