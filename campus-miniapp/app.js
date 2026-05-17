App({
  globalData: {
    baseUrl: require('./utils/env').baseUrl,
    userId: null,
    token: null,
    userInfo: null
  },

  onLaunch() {
    const userInfo = wx.getStorageSync('userInfo');
    const token = wx.getStorageSync('token');
    if (userInfo) {
      this.globalData.userInfo = userInfo;
      this.globalData.userId = userInfo.userId;
    }
    if (token) {
      this.globalData.token = token;
    }
  },

  setUserInfo(info) {
    this.globalData.userInfo = info;
    this.globalData.userId = info.userId;
    if (info.token) {
      this.globalData.token = info.token;
      wx.setStorageSync('token', info.token);
    }
    wx.setStorageSync('userInfo', info);
  },

  getToken() {
    return this.globalData.token;
  },

  getUserId() {
    return this.globalData.userId;
  },

  // 查看他人主页时设置的临时ID
  viewProfileId: null,
  // 当前学校（空=只看本校）
  currentSchool: '',

  setCurrentSchool(school) {
    this.currentSchool = school;
  },

  getCurrentSchool() {
    return this.currentSchool;
  },

  isLoggedIn() {
    return !!this.globalData.userId;
  },

  // 模拟登录（开发用，正式版替换为 wx.login）
  devLogin(cb) {
    const app = this;
    wx.login({
      success(res) {
        if (res.code) {
          wx.request({
            url: app.globalData.baseUrl + '/api/user/login',
            method: 'POST',
            data: {
              code: res.code,
              nickname: '校园用户',
              campus: '未设置学校'
            },
            success(loginRes) {
              if (loginRes.data.code === 200) {
                app.setUserInfo(loginRes.data.data);
                cb && cb(loginRes.data.data);
              }
            }
          });
        }
      }
    });
  }
});
