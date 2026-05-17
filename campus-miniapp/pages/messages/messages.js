const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    activeTab: 'chats',
    conversations: [],
    notifications: [],
    unreadChats: 0,
    unreadNotifs: 0
  },

  onShow() {
    if (!app.isLoggedIn()) {
      app.devLogin(() => this.loadData());
    } else {
      this.loadData();
    }
  },

  loadData() {
    const userId = app.getUserId();
    if (!userId) return;

    // 加载私信会话和未读数
    api.getConversations().then(convs => {
      let unread = 0;
      convs.forEach(c => unread += c.unread || 0);
      this.setData({ conversations: convs, unreadChats: unread });
    }).catch(() => {});

    // 加载通知列表
    api.getNotifications().then(notifs => {
      const unread = notifs.filter(n => !n.isRead).length;
      this.setData({ notifications: notifs, unreadNotifs: unread });
    }).catch(() => {});
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.tab });
  },

  goChat(e) {
    const userId = e.currentTarget.dataset.userId;
    const userName = e.currentTarget.dataset.userName;
    wx.navigateTo({ url: `/pages/chat/chat?userId=${userId}&userName=${userName}` });
  },

  markRead(e) {
    const id = e.currentTarget.dataset.id;
    const userId = app.getUserId();
    api.markNotifRead(id).then(() => {
      const notifications = this.data.notifications.map(n => {
        if (n.id === id) n.isRead = 1;
        return n;
      });
      this.setData({ notifications, unreadNotifs: Math.max(0, this.data.unreadNotifs - 1) });
    });
  },

  markAllRead() {
    api.markAllNotifsRead().then(() => {
      const notifications = this.data.notifications.map(n => { n.isRead = 1; return n; });
      this.setData({ notifications, unreadNotifs: 0 });
    });
  }
});
