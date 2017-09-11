package nanakusa.io;

import nanakusa.conf._
import scala.collection.mutable._
import com.github.tototoshi.csv._
import java.io._

class CsvIO {

  def outCsv(outLine : List[List[String]], fileName:String) = {
    println("CSV出力_開始");
    val f = new File(fileName);
    val writer = CSVWriter.open(f);
    writer.writeAll(outLine);
    writer.close();
    println("CSV出力_終了");
  }
  def inCsvAll(fileName:String):List[List[String]] = {
    val reader = CSVReader.open(new File(fileName));
    val inList = reader.all();
    reader.close();

    return inList;
  }

  def mageCompanyInfo() = {
    val listToMap = (ls:List[List[String]]) => ls.map(l => {Map(l(CsvLtl.st_code_col) -> l)});

    val tmpCpCsv = inCsvAll(CsvLtl.csv_tmp_cp_base);

    val tmpBspCsv = Map.empty[String ,List[String]];
    inCsvAll(CsvLtl.csv_tmp_bsp).foreach {l => {tmpBspCsv.put(l(CsvLtl.st_code_col), l)}};

    val tmpOrdCsv = Map.empty[String ,List[String]];
    inCsvAll(CsvLtl.csv_tmp_ord_profit).foreach{l => {tmpOrdCsv.put(l(CsvLtl.st_code_col), l)}};

    val mstMap = Map.empty[String ,List[String]];

    tmpCpCsv.foreach { cp => {
                              val code = cp(CsvLtl.st_code_col);


                              val mst_cp_name = cp(CsvLtl.mst_cp_name_col);
                              val mst_cp_place = cp(CsvLtl.mst_cp_place_col);
                              val mst_cp_type = cp(CsvLtl.mst_cp_type_col);

                              val bsp = tmpBspCsv getOrElse (code, List("","",""));
                              val mst_bsp = bsp(CsvLtl.bsp_bsp_col);
                              val mst_mini_pris = bsp(CsvLtl.bsp_mini_pris_col);
                              val mst_mini_cnt = bsp(CsvLtl.bsp_mini_cnt_col);

                              val ord = tmpOrdCsv getOrElse (code, List("",""));
                              val mst_last_profit = ord(CsvLtl.ord_last_profit_col);
                              val mst_now_profit = ord(CsvLtl.ord_last_profit_col);;

                              mstMap.put(code, List(code, mst_cp_name, mst_cp_place, mst_cp_type
                                                , mst_bsp, mst_mini_pris, mst_mini_cnt
                                                , mst_last_profit ,mst_now_profit));
                              }}

    outCsv(mstMap.values.toList, CsvLtl.csv_master_company_info);
  }

  def getStockCode(inFileName : String) : List[String] =  {
    val inList = inCsvAll(inFileName);
    val res = inList.map(in => in(0));

    return res;
  }

  def targetCsv() = {
    val inList = inCsvAll(CsvLtl.csv_master_company_info);

    val res = ListBuffer.empty[List[String]];
    val targetList = inList.map(in => {
                      if(jug_func(in)){
                        res += in;
                      }
                    });
    outCsv(res.toList ,CsvLtl.csv_watch_target);
  }


  def underLineAlertCsv() = {
    val tList = ListBuffer.empty[List[String]];
    getStockCode(CsvLtl.csv_watch_target).foreach{code => {
        val in = inCsvAll(CsvLtl.csv_daily_stock + code + ".csv");
        if(0 < in.size){
          tList += in(0);
        }
      }
    }
    val outList = ListBuffer.empty[List[String]];
    tList.foreach{l => {
        val under_line = l(6);
        under_line match {
            case num if num.toDouble < SetLtl.under_line_border => outList += l;
            case _ =>
        }
      }
    }
    outCsv(outList.toList ,CsvLtl.csv_alert_stock_code);
  }

  def jug_func(in : List[String]): Boolean = {
    if(!(toD_func(in(CsvLtl.mst_last_profit_col)) > 0)) {
      return false;
    }
    if(!(toD_func(in(CsvLtl.mst_now_profit_col)) > 0)) {
      return false;
    }
    if(!(toD_func(in(CsvLtl.mst_mini_pris_col)) <= SetLtl.mini_pris)) {
      return false;
    }

    if(!(toD_func(in(CsvLtl.mst_bsp_col)) > 0)) {
      return false;
    }
    if(!(toD_func(in(CsvLtl.mst_mini_cnt_col)) > 0)) {
      return false;
    }

    if(!(toD_func(in(CsvLtl.mst_mini_pris_col)) / toD_func(in(CsvLtl.mst_mini_cnt_col)) < toD_func(in(CsvLtl.mst_bsp_col)))) {
      return false;
    }
    return true;
  }

  def toD_func(str:String):Double = {
        val numChk_func = (str:String) => str.matches(""".*\d.*""");
        return if(numChk_func(str)) str.toDouble else -1.0
  }
}
