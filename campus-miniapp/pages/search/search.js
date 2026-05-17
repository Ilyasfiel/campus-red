const api = require('../../utils/api');
const app = getApp();

Page({
  data: {
    keyword: '',
    results: [],
    searched: false,
    hotTags: ['校园美食', '图书馆', '宿舍好物', '选课攻略', '社团活动', '考研自习', '附近探店', '体育健身', '拍照打卡', '食堂攻略', '樱花', '期末']
  },

  onLoad(options) {
    if (options.tag) {
      this.setData({ keyword: options.tag });
      this.searchByTag(options.tag);
    }
  },

  onInput(e) { this.setData({ keyword: e.detail.value }); },

  doSearch() {
    const kw = this.data.keyword.trim();
    if (!kw) return;
    // 去掉开头的#符号匹配标签
    const clean = kw.startsWith('#') ? kw.substring(1) : kw;
    this.setData({ searched: true, keyword: kw });
    // 优先用关键词搜索标题+内容
    api.getNotes(1, null, 'latest', null, clean).then(res => {
      const results = res.records || [];
      if (results.length === 0) {
        // 关键词无结果时回退到标签搜索
        api.getNotes(1, null, 'latest', clean).then(r => {
          this.setData({ results: r.records || [] });
        });
      } else {
        this.setData({ results });
      }
    });
  },

  searchByTag(tag) {
    this.setData({ searched: true, keyword: tag });
    api.getNotes(1, null, 'latest', tag).then(res => {
      this.setData({ results: res.records || [] });
    });
  },

  searchTag(e) {
    const tag = e.currentTarget.dataset.tag;
    this.setData({ keyword: '#' + tag });
    this.searchByTag(tag);
  },

  goDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/note-detail/note-detail?id=${id}` });
  }
});
