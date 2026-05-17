Component({
  data: {
    selected: 0,
    list: [
      { pagePath: '/pages/index/index', text: '首页', iconPath: '/images/tab-home.png', selectedIconPath: '/images/tab-home-active.png', badge: 0 },
      { pagePath: '/pages/treehole/treehole', text: '树洞', iconPath: '/images/tab-treehole.png', selectedIconPath: '/images/tab-treehole-active.png', badge: 0 },
      { pagePath: '/pages/publish/publish', text: '', iconPath: '/images/tab-publish-big.png', selectedIconPath: '/images/tab-publish-big.png', badge: 0 },
      { pagePath: '/pages/messages/messages', text: '消息', iconPath: '/images/tab-messages.png', selectedIconPath: '/images/tab-messages-active.png', badge: 0 },
      { pagePath: '/pages/profile/profile', text: '我的', iconPath: '/images/tab-profile.png', selectedIconPath: '/images/tab-profile-active.png', badge: 0 }
    ]
  },

  methods: {
    switchTab(e) {
      const { index, path } = e.currentTarget.dataset;
      if (this.data.selected === index) return;
      wx.switchTab({ url: path });
    },

    setBadge(index, count) {
      const key = `list[${index}].badge`;
      this.setData({ [key]: count || 0 });
    }
  }
});
