const app = getApp();
const env = require('./env');

const baseUrl = env.baseUrl;

function request(url, method = 'GET', data = {}, needAuth = true) {
  return new Promise((resolve, reject) => {
    const header = { 'Content-Type': 'application/json' };
    if (needAuth) {
      const token = app.getToken();
      if (token) {
        header['Authorization'] = 'Bearer ' + token;
      }
    }
    wx.request({
      url: baseUrl + url,
      method,
      data,
      header,
      success(res) {
        if (res.data.code === 200) {
          resolve(res.data.data);
        } else if (res.data.code === 401) {
          wx.showToast({ title: '请先登录', icon: 'none' });
          reject(res.data);
        } else {
          wx.showToast({ title: res.data.msg || '请求失败', icon: 'none' });
          reject(res.data);
        }
      },
      fail(err) {
        wx.showToast({ title: '网络错误', icon: 'none' });
        reject(err);
      }
    });
  });
}

const api = {
  // 用户
  login: (code) => request('/api/user/login', 'POST', {
    code,
    nickname: '校园用户',
    campus: '未设置学校'
  }, false),
  getSchools: () => request('/api/user/schools'),
  getUserProfile: (userId) =>
    request('/api/user/profile/' + userId),
  updateProfile: (data) => request('/api/user/profile', 'PUT', data),

  // 笔记
  getNotes: (page = 1, campus, sort = 'latest', tag, keyword) => {
    let url = '/api/notes?page=' + page + '&size=10&sort=' + sort;
    if (campus) url += '&campus=' + encodeURIComponent(campus);
    if (tag) url += '&tag=' + encodeURIComponent(tag);
    if (keyword) url += '&keyword=' + encodeURIComponent(keyword);
    return request(url);
  },
  getNoteDetail: (id) => request('/api/notes/' + id),
  createNote: (data) => request('/api/notes', 'POST', data),
  deleteNote: (id) => request('/api/notes/' + id, 'DELETE'),
  getUserNotes: (userId, page = 1) =>
    request('/api/notes/user/' + userId + '?page=' + page + '&size=10'),

  // 点赞
  toggleLike: (noteId) => request('/api/notes/' + noteId + '/like', 'POST'),

  // 收藏
  toggleCollect: (noteId) => request('/api/notes/' + noteId + '/collect', 'POST'),

  // 评论
  getComments: (noteId) => request('/api/comments/note/' + noteId),
  addComment: (noteId, content, parentId) =>
    request('/api/comments', 'POST', { noteId, content, parentId }),

  // 关注
  toggleFollow: (followeeId) =>
    request('/api/user/' + followeeId + '/follow', 'POST'),
  getFollowers: (userId) => request('/api/user/' + userId + '/followers'),
  getFollowing: (userId) => request('/api/user/' + userId + '/following'),

  // 上传
  uploadFile: (filePath) => {
    return new Promise((resolve, reject) => {
      const header = {};
      const token = app.getToken();
      if (token) {
        header['Authorization'] = 'Bearer ' + token;
      }
      wx.uploadFile({
        url: baseUrl + '/api/upload',
        filePath,
        name: 'file',
        header,
        success(res) {
          const data = JSON.parse(res.data);
          if (data.code === 200) resolve(data.data.url);
          else reject(data);
        },
        fail: reject
      });
    });
  },

  // 消息
  getConversations: () => request('/api/messages/conversations'),
  getMessagesWith: (partnerId) => request('/api/messages/with/' + partnerId),
  sendMessage: (toUserId, content) =>
    request('/api/messages/send', 'POST', { toUserId, content }),
  unreadMessageCount: () => request('/api/messages/unread'),
  getNotifications: () => request('/api/messages/notifications'),
  unreadNotifCount: () => request('/api/messages/notifications/unread'),
  markNotifRead: (id) => request('/api/messages/notifications/' + id + '/read', 'PUT'),
  markAllNotifsRead: () => request('/api/messages/notifications/read-all', 'PUT'),

  // 树洞
  getTreeHoles: (page = 1) => request('/api/treehole?page=' + page + '&size=20'),
  createTreeHole: (content, tag) => request('/api/treehole', 'POST', { content, tag }),
  likeTreeHole: (id) => request('/api/treehole/' + id + '/like', 'POST'),
  getTreeHoleComments: (id) => request('/api/treehole/' + id + '/comments'),
  addTreeHoleComment: (id, content) =>
    request('/api/treehole/' + id + '/comments', 'POST', { content })
};

module.exports = api;
