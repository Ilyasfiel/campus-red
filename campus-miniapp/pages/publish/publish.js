const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    images: [],
    title: '',
    content: '',
    tagText: '',
    location: '',
    submitting: false
  },

  onTitleInput(e) { this.setData({ title: e.detail.value }); },
  onContentInput(e) { this.setData({ content: e.detail.value }); },
  onTagInput(e) { this.setData({ tagText: e.detail.value }); },

  chooseImage() {
    const remain = 9 - this.data.images.length;
    wx.chooseMedia({
      count: remain,
      mediaType: ['image'],
      sizeType: ['compressed'],
      success: res => {
        const paths = res.tempFiles.map(f => f.tempFilePath);
        this.uploadImages(paths);
      }
    });
  },

  uploadImages(paths) {
    const promises = paths.map(p => api.uploadFile(p));
    wx.showLoading({ title: '上传中...' });
    Promise.all(promises).then(urls => {
      wx.hideLoading();
      this.setData({ images: [...this.data.images, ...urls] });
    }).catch(() => { wx.hideLoading(); wx.showToast({ title: '上传失败', icon: 'none' }); });
  },

  removeImage(e) {
    const idx = e.currentTarget.dataset.index;
    const images = this.data.images.filter((_, i) => i !== idx);
    this.setData({ images });
  },

  chooseLocation() {
    wx.chooseLocation({
      success: res => this.setData({ location: res.name || res.address }),
      fail: () => {} // 用户取消不做处理
    });
  },

  publish() {
    const { title, content, images, tagText, location } = this.data;
    if (!title.trim()) { wx.showToast({ title: '请输入标题', icon: 'none' }); return; }
    if (!app.isLoggedIn()) { wx.showToast({ title: '请先登录', icon: 'none' }); return; }

    const tags = tagText ? tagText.split(',').map(t => t.trim()).filter(Boolean) : [];

    this.setData({ submitting: true });
    api.createNote({
      title: title.trim(),
      content: content.trim(),
      images,
      tags,
      location
    }).then(() => {
      this.setData({ images: [], title: '', content: '', tagText: '', location: '', submitting: false });
      wx.showToast({ title: '发布成功' });
      setTimeout(() => {
        wx.switchTab({ url: '/pages/index/index' });
      }, 1500);
    }).catch(() => {
      this.setData({ submitting: false });
    });
  }
});
