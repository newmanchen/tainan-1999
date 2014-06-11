# Taninan 311 #

# 目標 #

希望透過人手一機的便利性, 快速上傳然後通報問題並追蹤處理的進度.

並結合網頁和後端追蹤系統讓不管在電腦上或是手機皆可以關心通報問題的進度

[Taiana 311](http://fixmystreet.tw/)

主要功能有

* 找出目前所有的問題
* 回報問題
* 了解當前處理的進度
* 追蹤有興趣的問題

# 通報問題類別 #

1. 道路問題
2. 路霸與騎樓佔用
3. 市容整潔 
4. 號誌、路燈故障
5. 其他

---------------
# 以下是開發相關 #


## 首頁，預估六個icon
 - Report a problem
 - My Reports  
 - Recent/Nearby report (不確定是哪一個)
 - ??
 - FAQ
 - About

### Report a problem流程
利用Wizard方式一步一步完成,預計頁面如下

 - Tip (說明回報的大概流程，不要再提示我的勾勾...自由發揮) <-- Sam Chiu
 - Map 從地圖挑選位置,並秀出附近的回報避免重複回報   <-- Sam Lee
 - Picture 透過camera或挑現成照片....  <-- vincent搞定
 - Form 回報內容資訊: 標題內文..有的沒的ooxx
 - Submit 顯示前面完成的資料,然後送到server  <--不確定需不需要多這一頁...