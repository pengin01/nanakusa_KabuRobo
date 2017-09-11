package nanakusa.conf;

object CsvLtl {

  val path_csv = "./csv/";
  val path_tmp = "./csv/tmp/";
  val path_daily = "./csv/daily/"

  val csv_tmp_cp_base = path_tmp + "tmp_cp_base.csv"
  val csv_tmp_bsp = path_tmp + "tmp_bsp.csv";
  // 経営利益
  val csv_tmp_ord_profit = path_tmp + "tmp_ord_profit.csv";

  val csv_master_company_info = path_csv + "master_company_info.csv";
  val csv_watch_target = path_csv + "watch_target.csv";
  val csv_alert_stock_code = path_csv + "alert_stock_code.csv";

  val csv_daily_stock = path_daily + "daily_stock_";


  val st_code_col = 0;

  // master_company_info.csv
  val cp_name_col = 1;
  val cp_place_col = 2;
  val cp_type_col = 3;

  // tmp_bsp.csv
  val bsp_bsp_col = 1;
  val bsp_mini_pris_col = 2;
  val bsp_mini_cnt_col = 3;

  // tmp_ord_profit.csv
  val ord_last_profit_col = 1;
  val ord_now_profit_col = 2;

  // master_company_info.csv
  val mst_cp_name_col = 1;
  val mst_cp_place_col = 2;
  val mst_cp_type_col = 3;
  val mst_bsp_col = 4;
  val mst_mini_pris_col = 5;
  val mst_mini_cnt_col = 6;
  val mst_last_profit_col = 7;
  val mst_now_profit_col = 8;


}
