##Tainan1999##

###目前進度
---------------
持續開發中

目前有一穩定版本發佈於 Google Play 上

<a href="https://play.google.com/store/apps/details?id=tn.opendata.tainan311" target="_blank">
  <img alt="Get it on Google Play"
       src="https://developer.android.com/images/brand/zh-tw_generic_rgb_wo_60.png" />
</a>

ps1. 如果想用 google map , 請自己設定 signinConfig 和 申請 API key

ps2. 現階段有遇到 API 設計不良的問題，流量會比較大 [Issue #9](https://github.com/kiang/1999.tainan.gov.tw/issues/9)

###想法###
---------------
台南市政府推行了 Open 1999 的線上回報系統，可以透過網頁回報相關的問題給政府機關，但礙於網頁介面不像手機，擁地理位置的資訊和隨拍即傳的便利性，所以這個 app 就產生了。

實際上在2014年我們參加了[台南黑客松-HackTainan](http://tdcp.kktix.cc/events/hacktainan2014/)的活動( 當時報名名稱為-台南311)就已經為這個想法落實且開發了一個相似的 app，只是背後的理論基礎是基於 [Open 311](http://www.open311.org/) 的協定。

經過了一年多的努力，台南市政府也推行了這項開放運動，將以前只能透過電話和網頁才能回報相關案件的平台，進而開放 [API](http://1999.tainan.gov.tw/OpenExplain.aspx) 讓大家都可以透過 API 讀取和新增案件。

也因為這樣我們就改接上了台南市政府的 API，讓這世代人手一機的我們，可以快速且準備的將案件回報給台南市民服務熱線線，進而改善台南市的街景市容，讓台南市變得更好。

###主要功能###
---------------
+ 回報問題 ( 如路燈故障、動物救援、道路維修、髒亂及污染等共九大類35項 )
+ 我的回報案件
+ 查詢進度

###特色###
---------------
+ 自動帶出必填的姓名、電話、地址 ( 透過經緯度反查 )
+ 自動帶出非必要欄位的 E-mail、經緯度
+ 即時拍照或從相簿中選取照片上傳
+ 設定預設姓名、電話、E-mail 以便在回報頁面可以自動帶出資料
+ 自動偵測地點或手動選取地點
+ 可以追蹤自己所回報的案件

###相關連結###
---------------
[甚麼是 Oepn311](http://www.open311.org/learn/)

[台南市民服務熱線](http://1999.tainan.gov.tw/)，也是所謂的台南-1999網頁版

###貢獻者###
---------------
[Newman Chen](https://github.com/newmanchen)

[Sam Lee](https://github.com/misgod)

[Vincent Nien]

[Sam Chiu](https://github.com/iamsamchiu)


###授權###
---------------
[Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0.html)
