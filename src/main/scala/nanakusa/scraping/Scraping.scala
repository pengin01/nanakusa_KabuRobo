package nanakusa.scraping;

import nanakusa.conf._
import scala.collection.mutable.ListBuffer
import org.jsoup._
import collection.JavaConverters._

class Scraping {


  // def testget() {
  //   val doc = Jsoup.connect("http://www.nicovideo.jp/watch/1488606698").get;
  //   val tags = doc.select(".MainVideoPlayer").asScala.toList;
  //
  //   tags.foreach{ tag => {
  //                               println(tag.text);
  //                         }
  //                       }
  // }

  //証券コード取得
  def getWeb(): List[List[String]] = {

    println("証券コード取得処理_開始");

    val url = "http://kabu-data.info/all_code/all_code_code.htm";
    val doc = Jsoup.connect(url).get;

    ////////////////////////////
    // HTMLdocumentをパースしたり書き換えたりする
    // bodyとか

    val head = doc.head;
    // 中身
    val textContent = doc.body.text // タグなしテキストのみ
    val htmlContent = doc.body.html // innerHTML

    // println(htmlContent)

    // select色々
    val tags = doc.select("tr").asScala.toList;
    // println(tags.getClass);

    val res = ListBuffer.empty[List[String]];
    tags.foreach{ tag => {
                                val spTag = (tag.text split ' ');
                                val stCode:String = spTag(0);

                                if (stCode.matches("""\d{4}""")) {
                                  val cpName:String = spTag(1);
                                  val mktName:String = (spTag(2) split ' ')(0);
                                  val mktCtg:String  = (spTag(2) split ' ')(1);
                                  res += List(stCode, cpName, mktName, mktCtg);
                                }
                          }
                        }


    // println(Json.arr(a))
    // println(res.toList.getClass);
    println("証券コード取得処理_終了");
    return res.toList;
  }

  def getDailyStock(code:String, today:String) : List[List[String]] = {

    val start_day = today;
    val end_day = today;
    return getDailyStock(code, start_day, end_day);
  }

  def getDailyStock(code:String, start_day:String, end_day:String) : List[List[String]] = {
    val sp_start_today = start_day split '/';
    val st_year = sp_start_today(0);
    val st_month = sp_start_today(1);
    val st_day = sp_start_today(2);

    val sp_end_day= end_day split '/';
    val ed_year = sp_end_day(0);
    val ed_month = sp_end_day(1);
    val ed_day = sp_end_day(2);

    val url = "https://info.finance.yahoo.co.jp/history/?code=" + code + "&sy=" + st_year + "&sm=" + st_month + "&sd=" + st_day + "&ey=" + ed_year + "&em=" + ed_month + "&ed=" + ed_day + "&tm=d";

    val numChk_func = (str:String) => str.matches(""".*\d.*""");
    val cov_num_func = (str:String) => {
      if(numChk_func(str)){
        str;
      }else{
        "-1";
      }
    }

    val res = ListBuffer.empty[List[String]];

    val doc = Jsoup.connect(url).get;

    val tags = doc.select(".marB6 tr").asScala.toList;
    tags.foreach{ tag=> {
        if((tag.text).matches(""".*年.*""")) {
          val sp_value = (tag.text.replaceAll(",", "") split ' ');
          val get_day = sp_value(0);
          val st_value = cov_num_func(sp_value(1));
          val max_value = cov_num_func(sp_value(2));
          val mini_value = cov_num_func(sp_value(3));
          val ed_value = cov_num_func(sp_value(4));

          val under_line = if(st_value < ed_value) mini_value.toDouble / st_value.toDouble else mini_value.toDouble / ed_value.toDouble
          println(List(code, get_day, st_value ,max_value ,mini_value ,ed_value ,under_line.toString));
          res += List(code, get_day, st_value ,max_value ,mini_value ,ed_value ,under_line.toString);
        }
      }
    }
    return res.toList;
  }


  def getCompanyBps(codeList : List[String]) : List[List[String]] = {
    println("BSP取得処理_開始");

    val res = ListBuffer.empty[List[String]];

    codeList.foreach{ code => {
      val doc = Jsoup.connect("https://stocks.finance.yahoo.co.jp/stocks/detail/?code=" + code).get;

      val tags = doc.select(".tseDtl").asScala.toList;

      val numChk_func = (str:String) => str.matches(""".*\d.*""");

      var miniBuyPrice = "";
      doc.select(".tseDtl").asScala.foreach{ tag => {

          println(tag.text);
          if((tag.text).matches(""".+最低購入代金.*""")){

            val mini = (tag.text split '（')(0).replaceAll(",", "");
            if(numChk_func(mini)){
              miniBuyPrice = mini;
            }else{
              miniBuyPrice = "-1";
            }
          }
        }
      }

      var bps = "";
      var miniCnt = "";
      doc.select(".tseDtlDelay").asScala.foreach{ tag => {

          if((tag.text).matches(""".+BPS.*""")){
            val tmp = (tag.text split '（')(0).replaceAll(",", "");
            if(numChk_func(tmp)){
              bps = (tmp split ' ')(1);
            } else {
              bps = "-1";
            }
          }
          if((tag.text).matches(""".+単元株数.*""")){
            val tmp = (tag.text split '（')(0).replaceAll(",", "");
            if(numChk_func(tmp)){
              miniCnt=(tmp split '株')(0);
            } else {
              miniCnt = "-1";
            }
          }
        }
      }
      // println(code + "：" + bps + "：" + miniBuyPrice + "：" + miniCnt));
      res += List(code, bps, miniBuyPrice , miniCnt);
    }}
    println("BSP取得処理_終了");
    return res.toList;
  }


  def getOrdinaryProfit(codeList : List[String]) : List[List[String]] = {
    println("経営利益_開始");

    val res = ListBuffer.empty[List[String]];

    codeList.foreach{ code => {
      val doc = Jsoup.connect("http://kabuyoho.ifis.co.jp/index.php?action=tp1&sa=report&bcode=" + code).get;
      val tags = doc.select(".tb_perform tr").asScala.toList;


      val numChk_func = (str:String) => str.matches(""".*\d.*""");

      var miniBuyPrice = "";
      tags.foreach { tag => {

          if((tag.text).matches("""経常利益.*""")){

            println(tag.text);

            var konki = "";
            val zenki = if(numChk_func((tag.text split ' ')(1).replaceAll(",", ""))){
              (tag.text split ' ')(1).replaceAll(",", "");
            }else{
              "-1";
            }

            if(numChk_func((tag.text split ' ')(2).replaceAll(",", ""))){
              konki = (tag.text split ' ')(2).replaceAll(",", "");
            }else{
              konki = "-1";
            }

            res += List(code, zenki, konki);
          }
        }
      }
    }}
    println("経営利益_終了");
    return res.toList;
  }
}
