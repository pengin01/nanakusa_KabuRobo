package nanakusa;

import conf._
import scraping._
import io._

object Job {
  def main(args: Array[String]): Unit = {

    val csv = new CsvIO();
    val sc = new Scraping();

    val logicName = args(0);
    logicName match {

      case "mk_mst_cp_inf" => {
        // 企業情報
        val outLine = sc.getWeb();
        csv.outCsv(outLine , CsvLtl.csv_tmp_cp_base);

        // BSP
        val companyBpsLine = sc.getCompanyBps(csv.getStockCode(CsvLtl.csv_tmp_cp_base));
        csv.outCsv(companyBpsLine , CsvLtl.csv_tmp_bsp);

        // 経営利益
        val ordinaryProfitLine = sc.getOrdinaryProfit(csv.getStockCode(CsvLtl.csv_tmp_cp_base));
        csv.outCsv(ordinaryProfitLine , CsvLtl.csv_tmp_ord_profit);

        // 企業情報マスタ作成
        csv.mageCompanyInfo();
      }

      case "mk_watch_tgt" => {
        // 監視対象株式
        csv.targetCsv();
      }

      case "al_today" => {
        val startDay = args(1);
        // 上昇の可能性がある銘柄：1日分
        csv.getStockCode(CsvLtl.csv_watch_target).foreach{code => {
            val outList = sc.getDailyStock(code, startDay);
            csv.outCsv(outList, CsvLtl.csv_daily_stock + code + ".csv");

            csv.underLineAlertCsv();
          }
        }
      }

      case "span" => {
        val startDay = args(1);
        val endDay = args(2);

        // 監視対象銘柄を期間指定で出力:max25営業日
        csv.getStockCode(CsvLtl.csv_watch_target).foreach{code => {
            val outList = sc.getDailyStock(code, startDay, endDay);
            // val outList = sc.getDailyStock(code, "2017/3/1", "2017/3/29");
            csv.outCsv(outList, CsvLtl.csv_daily_stock + code + ".csv");
          }
        }
      }
      case _ => println("arg error");
    }


  }
}
