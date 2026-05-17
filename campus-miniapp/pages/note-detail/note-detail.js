const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    note: null,
    comments: [],
    commentText: '',
    loading: true,
    isSelf: false
  },

  onLoad(options) {
    const id = options.id;
    if (!app.isLoggedIn()) {
      app.devLogin(() => this.loadData(id));
    } else {
      this.loadData(id);
    }
  },

  loadData(id) {
    const userId = app.getUserId();
    Promise.all([
      api.getNoteDetail(id),
      api.getComments(id)
    ]).then(([note, comments]) => {
      this.setData({
        note, comments, loading: false,
        isSelf: note.user && note.user.id === userId
      });
    }).catch(() => this.setData({ loading: false }));
  },

  toggleLike() {
    if (!this.data.note) return;
    const note = this.data.note;
    api.toggleLike(note.id).then(res => {
      note.isLiked = res.liked;
      note.likeCount += res.liked ? 1 : -1;
      this.setData({ note });
    });
  },

  toggleCollect() {
    if (!this.data.note) return;
    const note = this.data.note;
    api.toggleCollect(note.id).then(res => {
      note.isCollected = res.collected;
      note.collectCount += res.collected ? 1 : -1;
      this.setData({ note });
      wx.showToast({ title: res.collected ? '已收藏' : '已取消收藏', icon: 'none', duration: 1000 });
    });
  },

  toggleFollow() {
    const note = this.data.note;
    if (!note || !note.user) return;
    api.toggleFollow(note.user.id).then(res => {
      const n = { ...this.data.note };
      n.user = { ...n.user, isFollowed: res.following };
      this.setData({ note: n });
    });
  },

  scrollToComments() {
    wx.pageScrollTo({ selector: '#comments', duration: 300 });
  },

  onCommentInput(e) { this.setData({ commentText: e.detail.value }); },

  sendComment() {
    const text = this.data.commentText.trim();
    if (!text) return;
    api.addComment(this.data.note.id, text).then(() => {
      this.setData({ commentText: '' });
      return api.getComments(this.data.note.id);
    }).then(comments => {
      this.setData({ comments });
      const note = this.data.note;
      note.commentCount = (note.commentCount || 0) + 1;
      this.setData({ note });
    });
  },

  goProfile(e) {
    const id = e.currentTarget.dataset.id;
    if (!id) return;
    app.viewProfileId = id;
    wx.switchTab({ url: '/pages/profile/profile' });
  },

  searchTag(e) {
    const tag = e.currentTarget.dataset.tag;
    if (tag) {
      wx.navigateTo({ url: `/pages/search/search?tag=${encodeURIComponent(tag)}` });
    }
  },

  onShareAppMessage() {
    const note = this.data.note;
    return {
      title: note ? note.title : '校园红书',
      path: `/pages/note-detail/note-detail?id=${note ? note.id : ''}`,
      imageUrl: note && note.images && note.images.length > 0 ? note.images[0] : ''
    };
  }
});
