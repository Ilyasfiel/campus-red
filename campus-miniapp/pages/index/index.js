const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    notes: [],
    leftNotes: [],
    rightNotes: [],
    page: 1,
    loading: true,
    hasMore: true,
    currentSchool: '',
    myCampus: '',
    schools: [],
    sort: 'latest',
    sortLabel: '最新'
  },

  onLoad() {
    if (!app.isLoggedIn()) {
      app.devLogin(() => this.init());
    } else {
      this.init();
    }
  },

  onShow() {
    if (app.isLoggedIn() && this.data.notes.length === 0 && !this.data.loading) {
      this.init();
    }
    this.updateTabBadge();
  },

  updateTabBadge() {
    const userId = app.getUserId();
    if (!userId) return;
    Promise.all([
      api.unreadMessageCount(),
      api.unreadNotifCount()
    ]).then(([msgRes, notifRes]) => {
      const total = (msgRes.count || 0) + (notifRes.count || 0);
      const tabBar = this.getTabBar();
      if (tabBar) tabBar.setBadge(3, total);
    }).catch(() => {});
  },

  init() {
    const userInfo = app.globalData.userInfo;
    this.setData({
      myCampus: userInfo ? userInfo.campus || '' : '',
      currentSchool: app.getCurrentSchool() || '河北外国语学院'
    });
    this.loadSchools();
    this.setData({ page: 1, notes: [], hasMore: true });
    this.loadNotes();
  },

  loadSchools() {
    api.getSchools().then(schools => this.setData({ schools: schools || [] }));
  },

  switchSchool() {
    const schools = this.data.schools;
    const items = ['全部学校（本校优先）', ...schools];
    wx.showActionSheet({
      itemList: items,
      success: res => {
        const idx = res.tapIndex;
        const selected = idx === 0 ? '' : items[idx];
        app.setCurrentSchool(selected);
        this.setData({
          currentSchool: selected,
          page: 1,
          notes: [],
          hasMore: true
        });
        this.loadNotes();
      }
    });
  },

  onPullDownRefresh() {
    this.setData({ page: 1, notes: [], hasMore: true });
    this.loadNotes().then(() => wx.stopPullDownRefresh());
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadNotes();
    }
  },

  toggleSort() {
    const sort = this.data.sort === 'latest' ? 'hot' : 'latest';
    this.setData({ sort, sortLabel: sort === 'hot' ? '🔥最热' : '最新', page: 1, notes: [], hasMore: true });
    this.loadNotes();
  },

  loadNotes() {
    if (!this.data.hasMore || this.data.loading && this.data.page > 1) return;
    this.setData({ loading: true });
    const campus = this.data.currentSchool;
    return api.getNotes(this.data.page, campus, this.data.sort).then(res => {
      const notes = this.data.page === 1 ? res.records : [...this.data.notes, ...res.records];
      const leftNotes = notes.filter((_, i) => i % 2 === 0);
      const rightNotes = notes.filter((_, i) => i % 2 === 1);
      this.setData({
        notes,
        leftNotes,
        rightNotes,
        page: this.data.page + 1,
        hasMore: res.records.length === 10,
        loading: false
      });
    }).catch(() => this.setData({ loading: false }));
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/note-detail/note-detail?id=${id}` });
  },

  goSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  }
});
