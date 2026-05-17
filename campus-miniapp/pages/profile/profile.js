const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    profile: null,
    userNotes: [],
    leftNotes: [],
    rightNotes: [],
    loading: true,
    isSelf: false,
    profileId: null,
    showListModal: false,
    listTitle: '',
    userList: []
  },

  onLoad(options) {
    // 优先从全局获取（其他页面通过switchTab跳转过来的）
    const viewId = app.viewProfileId;
    const profileId = options.id || viewId || app.getUserId();
    app.viewProfileId = null; // 清除
    if (!profileId) {
      app.devLogin(() => this.loadProfile(profileId));
      return;
    }
    this.setData({ profileId });
    this.loadProfile(profileId);
  },

  onShow() {
    const viewId = app.viewProfileId;
    const myId = app.getUserId();
    if (viewId) {
      app.viewProfileId = null;
      this.setData({ profileId: viewId });
      this.loadProfile(viewId);
    } else if (!this.data.profileId) {
      this.setData({ profileId: myId });
      this.loadProfile(myId);
    }
  },

  loadProfile(userId) {
    const currentUserId = app.getUserId();
    this.setData({ loading: true });

    Promise.all([
      api.getUserProfile(userId),
      api.getUserNotes(userId, 1)
    ]).then(([profile, notesData]) => {
      const notes = notesData.records || [];
      const leftNotes = notes.filter((_, i) => i % 2 === 0);
      const rightNotes = notes.filter((_, i) => i % 2 === 1);
      this.setData({
        profile,
        userNotes: notes,
        leftNotes,
        rightNotes,
        loading: false,
        isSelf: currentUserId === userId
      });
    }).catch(() => this.setData({ loading: false }));
  },

  toggleFollow() {
    const profile = this.data.profile;
    if (!profile) return;
    api.toggleFollow(profile.id).then(res => {
      const p = { ...this.data.profile };
      p.isFollowed = res.following;
      p.followerCount += res.following ? 1 : -1;
      this.setData({ profile: p });
    });
  },

  editProfile() {
    wx.showActionSheet({
      itemList: ['修改昵称', '修改学校', '修改简介'],
      success(res) {
        const fields = ['nickname', 'campus', 'bio'];
        const labels = ['昵称', '学校', '简介'];
        const field = fields[res.tapIndex];
        wx.showModal({
          title: `修改${labels[res.tapIndex]}`,
          editable: true,
          placeholderText: `输入新的${labels[res.tapIndex]}`,
          success(modalRes) {
            if (modalRes.confirm) {
              const data = {};
              data[field] = modalRes.content;
              api.updateProfile(data).then(() => {
                wx.showToast({ title: '修改成功', icon: 'success' });
              });
            }
          }
        });
      }
    });
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/note-detail/note-detail?id=${id}` });
  },

  showFollowList(e) {
    const type = e.currentTarget.dataset.type;
    const title = type === 'followers' ? '粉丝' : '关注';
    const apiMethod = type === 'followers' ? api.getFollowers : api.getFollowing;
    apiMethod(this.data.profileId).then(list => {
      this.setData({ showListModal: true, listTitle: title, userList: list || [] });
    });
  },

  closeListModal() { this.setData({ showListModal: false }); },

  goUserProfile(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ showListModal: false });
    app.viewProfileId = id;
    wx.switchTab({ url: '/pages/profile/profile' });
  }
});
