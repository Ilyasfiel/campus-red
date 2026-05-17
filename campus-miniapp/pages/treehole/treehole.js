const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    holes: [],
    loading: true,
    expandedId: null,
    commentText: '',
    showModal: false,
    publishText: '',
    tagIndex: 0,
    tags: ['', '😊 开心', '😢 难过', '😠 生气', '💔 失恋', '📚 学业', '💼 求职', '🤔 困惑', '😂 搞笑', '🎉 庆祝']
  },

  onLoad() { this.loadHoles(); },

  loadHoles() {
    this.setData({ loading: true });
    api.getTreeHoles().then(data => {
      const holes = (data.records || []).map(h => { h._comments = []; return h; });
      this.setData({ holes, loading: false });
    }).catch(() => this.setData({ loading: false }));
  },

  likeHole(e) {
    const id = e.currentTarget.dataset.id;
    api.likeTreeHole(id).then(res => {
      const holes = this.data.holes.map(h => {
        if (h.id === id) h.likeCount = res.likeCount;
        return h;
      });
      this.setData({ holes });
    });
  },

  toggleComments(e) {
    const id = e.currentTarget.dataset.id;
    if (this.data.expandedId === id) {
      this.setData({ expandedId: null });
      return;
    }
    api.getTreeHoleComments(id).then(comments => {
      const holes = this.data.holes.map(h => {
        if (h.id === id) h._comments = comments;
        return h;
      });
      this.setData({ holes, expandedId: id });
    });
  },

  onCommentInput(e) { this.setData({ commentText: e.detail.value }); },

  sendComment(e) {
    const text = this.data.commentText.trim();
    if (!text) return;
    const id = e.currentTarget.dataset.id || this.data.expandedId;
    api.addTreeHoleComment(id, text).then(() => {
      this.setData({ commentText: '' });
      return api.getTreeHoleComments(id);
    }).then(comments => {
      const holes = this.data.holes.map(h => {
        if (h.id === id) {
          h._comments = comments;
          h.commentCount = (h.commentCount || 0) + 1;
        }
        return h;
      });
      this.setData({ holes });
    });
  },

  showPublish() { this.setData({ showModal: true, publishText: '', tagIndex: 0 }); },
  hideModal() { this.setData({ showModal: false }); },
  onTagChange(e) { this.setData({ tagIndex: parseInt(e.detail.value) }); },
  onPublishInput(e) { this.setData({ publishText: e.detail.value }); },

  publish() {
    const text = this.data.publishText.trim();
    if (!text) { wx.showToast({ title: '写点什么吧', icon: 'none' }); return; }
    const tag = this.data.tags[this.data.tagIndex] || '';
    api.createTreeHole(text, tag).then(() => {
      this.setData({ showModal: false, publishText: '' });
      wx.showToast({ title: '已匿名发布', icon: 'success' });
      this.loadHoles();
    });
  }
});
