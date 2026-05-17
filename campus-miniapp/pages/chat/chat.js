const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    messages: [],
    inputText: '',
    myUserId: null,
    partnerId: null,
    partnerName: ''
  },

  onLoad(options) {
    const partnerName = options.userName || '聊天';
    wx.setNavigationBarTitle({ title: partnerName });

    this.setData({
      myUserId: app.getUserId(),
      partnerId: parseInt(options.userId),
      partnerName
    });

    if (!this.data.myUserId) {
      app.devLogin(() => this.loadMessages());
    } else {
      this.loadMessages();
    }

    // 定时刷新
    this.refreshTimer = setInterval(() => this.loadMessages(), 3000);
  },

  onUnload() {
    if (this.refreshTimer) clearInterval(this.refreshTimer);
  },

  loadMessages() {
    const { myUserId, partnerId } = this.data;
    if (!myUserId || !partnerId) return;
    api.getMessagesWith(partnerId).then(msgs => {
      this.setData({ messages: msgs });
    }).catch(() => {});
  },

  onInput(e) { this.setData({ inputText: e.detail.value }); },

  send() {
    const text = this.data.inputText.trim();
    if (!text) return;
    const { myUserId, partnerId } = this.data;
    if (!myUserId || !partnerId) return;

    api.sendMessage(partnerId, text).then(() => {
      this.setData({ inputText: '' });
      this.loadMessages();
    });
  }
});
