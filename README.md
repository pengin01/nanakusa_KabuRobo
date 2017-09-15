# 七草の株ロボ

1. sbtを頑張ってインストールする。[sbt のインストール]( http://www.scala-sbt.org/0.13/docs/ja/Setup.html)  
※jarをダウンロードしてパスを通せばインストールしなくてもよい
1. このページから「Clone or Download」でDownlaodZipでダウンロードする
1. 解凍する
1. nanakusa_KabuRobo配下に移動
1. `sbt "run mk_mst_cp_inf"`<=すごく時間がかかる
1. `sbt "run mk_watch_tgt"`
1. `sbt "run al_today 2017/9/12"`
1. `nanakusa_KabuRobo/csv`に近々株価が上がる可能性がある証券コードが吐かれるよ（対象がない場合は空のファイル）
