# 自行車訓練與騎乘輔助 App（Team E）

將自行車功率裝置、手機感測資料、路線導航與 AI 分析服務整合為一套 Android 騎乘訓練流程。

## 專案資訊

- **開發期間：** 2024-06-14 ～ 2024-08-11
- **專案性質：** Team E 課程團隊專案
- **專題來源：** 財團法人自行車暨健康科技工業研究發展中心產業研習專題開發
- **應用領域：** 自行車訓練、即時功率監測與騎乘輔助
- **主要技術：** Java、Android SDK、BLE GATT、GPS、Google Maps、OkHttp、Apache POI

## 專案亮點

這個專案的重點不只是使用 BLE、GPS 或地圖 API，而是把不同更新頻率、不同連線方式的硬體與服務整合成可操作的訓練產品流程：

1. 使用者選擇室內 20 分鐘、室內 60 分鐘或戶外騎乘模式。
2. App 透過 BLE GATT 連接自行車功率裝置，接收即時功率資料。
3. 系統將功率分配至不同區間，累計各區間的訓練時間並產生 FTP 相關結果。
4. 戶外模式結合 GPS，呈現速度、位置與坡度資訊。
5. 路線頁面整合距離、海拔、坡度、起終點及 Google Maps 導航。
6. 訓練結果可匯出為 Excel，並透過 HTTP 呼叫外部 AI 預測服務。

## 技術挑戰與解決方式

- **非同步裝置連線：** 使用 Android BLE GATT 處理裝置連線、Service Discovery、Characteristic Notification 與 Cycling Power Measurement 封包。
- **多來源資料同步：** 整合不同更新頻率的 BLE 功率、GPS 位置與訓練計時資料，再同步呈現在 Android UI。
- **多種訓練情境：** 依室內短時間、室內長時間與戶外騎乘需求，提供不同計時、功率與路線流程。
- **跨服務整合：** 將行動裝置、功率硬體、Google Maps、Excel 匯出與 Colab AI 模型串成端到端流程。
- **實際環境限制：** 處理 Android 藍牙與定位權限、裝置識別、網路服務 URL 及外部 API 設定。

## 系統資料流

```text
自行車功率裝置 ── BLE ──> Android App ── HTTP ──> AI 預測服務
                              │
                              ├── GPS ──> 速度／位置／坡度
                              ├── Google Maps ──> 路線導航
                              └── Apache POI ──> Excel 訓練紀錄
```

## 專案畫面


### BLE 即時功率與計時

<p align="center">
  <img src="https://github.com/user-attachments/assets/cc8517d0-e3f9-4609-8c42-db89fca1b980" alt="BLE 即時功率與計時畫面" width="760">
</p>

### 路線資訊選擇清單

<p align="center">
  <img src="https://github.com/user-attachments/assets/67c80469-19c0-4d56-ae48-eeca105c9227" alt="路線資訊選擇清單畫面" width="520">
</p>

### FTP 計算與等級預測結果

<p align="center">
  <img src="https://github.com/user-attachments/assets/1af4222b-aad9-4825-ad83-210062debf26" alt="FTP 計算與等級預測結果畫面" width="320">
</p>


## AI 模型服務設定

[removed]

(AI model)Run Colab first. And get the URL.
Paste to
![image](https://github.com/user-attachments/assets/ce713677-69f0-4723-b5e7-7486e4476fae)
(https://....../predict)

