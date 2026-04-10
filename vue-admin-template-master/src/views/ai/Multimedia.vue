<template>
  <div class="ai-multimedia-container">
    <!-- 页面标题 -->
    <div class="header">
      <div class="logo-container">
        <div class="logo-placeholder">🎬</div>
        <div class="title-container">
          <h1 class="main-title">AI多媒体创作中心</h1>
          <p class="subtitle">文字生成视频 | 图生视频 | 视频特效 | 文字转语音</p>
        </div>
      </div>
    </div>

    <!-- 功能选择标签页 -->
    <div class="tabs-container">
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="文字生成视频" name="textToVideo">
          <i class="el-icon-video-camera"></i> 文字生成视频
        </el-tab-pane>
        <el-tab-pane label="图生视频" name="imageToVideo">
          <i class="el-icon-picture-outline"></i> 图生视频
        </el-tab-pane>
        <el-tab-pane label="视频特效" name="videoEffect">
          <i class="el-icon-magic-stick"></i> 视频特效
        </el-tab-pane>
        <el-tab-pane label="文字转语音" name="textToAudio">
          <i class="el-icon-headset"></i> 文字转语音
        </el-tab-pane>
      </el-tabs>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧：输入区域 -->
      <div class="input-section">
        <div class="card">
          <div class="card-header">
            <i :class="currentConfig.icon"></i>
            <span>{{ currentConfig.title }}</span>
          </div>
          <div class="card-body">
            <!-- 文字生成视频 -->
            <div v-if="activeTab === 'textToVideo'" class="form-section">
              <el-form label-width="100px" size="small">
                <el-form-item label="视频描述">
                  <el-input
                    v-model="formData.textToVideo.prompt"
                    type="textarea"
                    :rows="6"
                    placeholder="请输入视频场景描述..."
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <div class="quick-prompts">
                    <p class="quick-title">快速选择：</p>
                    <div class="prompt-chips">
                      <span 
                        v-for="(prompt, index) in videoPrompts" 
                        :key="index" 
                        class="prompt-chip"
                        @click="formData.textToVideo.prompt = prompt"
                      >
                        {{ prompt }}
                      </span>
                    </div>
                  </div>
                </el-form-item>
              </el-form>
            </div>

            <!-- 图生视频 -->
            <div v-if="activeTab === 'imageToVideo'" class="form-section">
              <el-alert
                title="提示：使用预设的图片和音频生成动态视频"
                type="info"
                :closable="false"
                show-icon
                style="margin-bottom: 15px;"
              ></el-alert>
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="图片URL">
                  <el-input v-model="formData.imageToVideo.imgUrl" disabled></el-input>
                </el-descriptions-item>
                <el-descriptions-item label="音频URL">
                  <el-input v-model="formData.imageToVideo.audioUrl" disabled></el-input>
                </el-descriptions-item>
                <el-descriptions-item label="视频描述">
                  <el-input 
                    v-model="formData.imageToVideo.prompt" 
                    type="textarea"
                    :rows="4"
                    disabled
                  ></el-input>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 视频特效 -->
            <div v-if="activeTab === 'videoEffect'" class="form-section">
              <el-alert
                title="提示：为静态图片添加汉服特效动画"
                type="info"
                :closable="false"
                show-icon
                style="margin-bottom: 15px;"
              ></el-alert>
              <el-descriptions :column="1" border size="small">
                <el-descriptions-item label="图片URL">
                  <el-input v-model="formData.videoEffect.firstFrameUrl" disabled></el-input>
                </el-descriptions-item>
                <el-descriptions-item label="特效模板">
                  <el-tag type="success">hanfu-1（汉服特效）</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="分辨率">
                  <el-tag>480P</el-tag>
                </el-descriptions-item>
                <el-descriptions-item label="时长">
                  <el-tag>5秒</el-tag>
                </el-descriptions-item>
              </el-descriptions>
            </div>

            <!-- 文字转语音 -->
            <div v-if="activeTab === 'textToAudio'" class="form-section">
              <el-form label-width="100px" size="small">
                <el-form-item label="文本内容">
                  <el-input
                    v-model="formData.textToAudio.text"
                    type="textarea"
                    :rows="6"
                    placeholder="请输入要转换为语音的文本..."
                  ></el-input>
                </el-form-item>
                <el-form-item>
                  <div class="quick-prompts">
                    <p class="quick-title">快速选择：</p>
                    <div class="prompt-chips">
                      <span 
                        v-for="(text, index) in audioTexts" 
                        :key="index" 
                        class="prompt-chip"
                        @click="formData.textToAudio.text = text"
                      >
                        {{ text.substring(0, 15) }}...
                      </span>
                    </div>
                  </div>
                </el-form-item>
                <el-form-item label="音色选择">
                  <el-tag type="primary">longxiaochun_v2（龙小春）</el-tag>
                </el-form-item>
              </el-form>
            </div>

            <!-- 生成按钮 -->
            <div class="action-buttons">
              <button 
                class="generate-btn" 
                @click="generateContent"
                :disabled="!canGenerate || isLoading"
                :class="{ disabled: !canGenerate || isLoading }"
              >
                <i class="icon-generate"></i>
                <span v-if="isLoading">生成中...</span>
                <span v-else>{{ currentConfig.buttonText }}</span>
              </button>
              
              <button 
                v-if="isLoading"
                class="cancel-btn" 
                @click="cancelGeneration"
              >
                <i class="icon-cancel"></i> 取消
              </button>
            </div>
          </div>
        </div>

        <!-- 历史记录 -->
        <div class="card history-card" v-if="historyList.length > 0">
          <div class="card-header">
            <i class="el-icon-time"></i>
            <span>生成历史</span>
            <button class="clear-history-btn" @click="clearHistory" title="清空历史">
              <i class="el-icon-delete"></i>
            </button>
          </div>
          <div class="card-body history-list">
            <div 
              v-for="(item, index) in historyList" 
              :key="index" 
              class="history-item"
              @click="viewHistory(item)"
            >
              <div class="history-icon">
                <i :class="item.typeIcon"></i>
              </div>
              <div class="history-info">
                <p class="history-type">{{ item.typeName }}</p>
                <p class="history-prompt">{{ item.prompt }}</p>
                <span class="history-time">{{ item.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：结果展示区域 -->
      <div class="result-section">
        <div class="card result-card">
          <div class="card-header">
            <i class="el-icon-view"></i>
            <span>生成结果</span>
          </div>
          <div class="card-body result-body">
            <!-- 初始状态 -->
            <div v-if="!currentResult && !isLoading" class="empty-state">
              <div class="empty-icon">{{ currentConfig.emptyIcon }}</div>
              <p>{{ currentConfig.emptyText }}</p>
              <p class="empty-tip">{{ currentConfig.emptyTip }}</p>
            </div>

            <!-- 加载状态 -->
            <div v-if="isLoading" class="loading-state">
              <div class="loading-spinner">
                <div class="spinner"></div>
              </div>
              <p>AI正在创作中，请稍候...</p>
              <p class="loading-tip">这可能需要几分钟时间</p>
            </div>

            <!-- 结果展示 -->
            <div v-if="currentResult && !isLoading" class="result-display">
              <!-- 视频结果 -->
              <div v-if="currentResult.type === 'video'" class="video-result">
                <video 
                  :src="currentResult.url" 
                  controls 
                  class="result-video"
                  @error="handleMediaError"
                >
                  您的浏览器不支持视频播放
                </video>
                <div class="result-info">
                  <p><strong>描述：</strong>{{ currentResult.prompt }}</p>
                  <p><strong>生成时间：</strong>{{ currentResult.time }}</p>
                </div>
                <div class="result-actions">
                  <a :href="currentResult.url" target="_blank" download class="action-btn download-btn">
                    <i class="el-icon-download"></i> 下载视频
                  </a>
                  <button class="action-btn regenerate-btn" @click="regenerate">
                    <i class="el-icon-refresh"></i> 重新生成
                  </button>
                </div>
              </div>

              <!-- 音频结果 -->
              <div v-if="currentResult.type === 'audio'" class="audio-result">
                <div class="audio-icon">🎵</div>
                <p class="audio-text">{{ currentResult.prompt }}</p>
                <audio 
                  :src="currentResult.url" 
                  controls 
                  class="result-audio"
                  @error="handleMediaError"
                >
                  您的浏览器不支持音频播放
                </audio>
                <div class="result-info">
                  <p><strong>生成时间：</strong>{{ currentResult.time }}</p>
                </div>
                <div class="result-actions">
                  <a :href="currentResult.url" target="_blank" download class="action-btn download-btn">
                    <i class="el-icon-download"></i> 下载音频
                  </a>
                  <button class="action-btn regenerate-btn" @click="regenerate">
                    <i class="el-icon-refresh"></i> 重新生成
                  </button>
                </div>
              </div>

              <!-- 文本结果（如TTS成功消息） -->
              <div v-if="currentResult.type === 'text'" class="text-result">
                <div class="success-icon">✅</div>
                <p class="success-message">{{ currentResult.message }}</p>
                <div class="result-info">
                  <p><strong>生成时间：</strong>{{ currentResult.time }}</p>
                </div>
                <div class="result-actions">
                  <button class="action-btn regenerate-btn" @click="regenerate">
                    <i class="el-icon-refresh"></i> 重新生成
                  </button>
                </div>
              </div>
            </div>

            <!-- 错误状态 -->
            <div v-if="errorMessage" class="error-state">
              <div class="error-icon">❌</div>
              <p class="error-message">{{ errorMessage }}</p>
              <button class="retry-btn" @click="generateContent">
                <i class="el-icon-refresh"></i> 重试
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'AIMultimedia',
  
  data() {
    return {
      activeTab: 'textToVideo',
      isLoading: false,
      errorMessage: '',
      currentResult: null,
      historyList: [],
      
      // 表单数据
      formData: {
        textToVideo: {
          prompt: '一幅史诗级可爱的场景。一只小巧可爱的卡通小猫将军，身穿细节精致的金色盔甲，头戴一个稍大的头盔，勇敢地站在悬崖上。他骑着一匹虽小但英勇的战马，说："青海长云暗雪山，孤城遥望玉门关。黄沙百战穿金甲，不破楼兰终不还。"。悬崖下方，一支由老鼠组成的、数量庞大、无穷无尽的军队正带着临时制作的武器向前冲锋。这是一个戏剧性的、大规模的战斗场景，灵感来自中国古代的战争史诗。远处的雪山上空，天空乌云密布。整体氛围是"可爱"与"霸气"的搞笑和史诗般的融合。'
        },
        imageToVideo: {
          imgUrl: 'https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/wpimhv/rap.png',
          audioUrl: 'https://help-static-aliyun-doc.aliyuncs.com/file-manage-files/zh-CN/20250925/ozwpvi/rap.mp3',
          prompt: '一幅都市奇幻艺术的场景。一个充满动感的涂鸦艺术角色。一个由喷漆所画成的少年，正从一面混凝土墙上活过来。他一边用极快的语速演唱一首英文rap，一边摆着一个经典的、充满活力的说唱歌手姿势。场景设定在夜晚一个充满都市感的铁路桥下。灯光来自一盏孤零零的街灯，营造出电影般的氛围，充满高能量和惊人的细节。视频的音频部分完全由他的rap构成，没有其他对话或杂音。'
        },
        videoEffect: {
          firstFrameUrl: 'https://ty-yuanfang.oss-cn-hangzhou.aliyuncs.com/lizhengjia.lzj/tmp/11.png'
        },
        textToAudio: {
          text: '静夜思 唐 李白 床前明月光,疑是地上霜,举头望明月,低头思故乡'
        }
      },
      
      // 快速提示词
      videoPrompts: [
        '可爱的小猫在花园里玩耍',
        '美丽的海滩日落景色',
        '未来科技城市夜景',
        '春天的樱花飘落',
        '太空中的星球旋转'
      ],
      
      audioTexts: [
        '静夜思 唐 李白 床前明月光,疑是地上霜,举头望明月,低头思故乡',
        '春晓 唐 孟浩然 春眠不觉晓,处处闻啼鸟,夜来风雨声,花落知多少',
        '登鹳雀楼 唐 王之涣 白日依山尽,黄河入海流,欲穷千里目,更上一层楼'
      ],
      
      // 配置信息
      configs: {
        textToVideo: {
          title: '文字生成视频',
          icon: 'el-icon-video-camera',
          buttonText: '生成视频',
          emptyIcon: '🎬',
          emptyText: '输入视频描述后点击"生成视频"',
          emptyTip: 'AI将根据您的描述创作精彩视频'
        },
        imageToVideo: {
          title: '图生视频',
          icon: 'el-icon-picture-outline',
          buttonText: '生成视频',
          emptyIcon: '🎥',
          emptyText: '点击"生成视频"开始创作',
          emptyTip: 'AI将让静态图片动起来'
        },
        videoEffect: {
          title: '视频特效',
          icon: 'el-icon-magic-stick',
          buttonText: '生成特效视频',
          emptyIcon: '✨',
          emptyText: '点击"生成特效视频"开始创作',
          emptyTip: 'AI将为图片添加精美特效'
        },
        textToAudio: {
          title: '文字转语音',
          icon: 'el-icon-headset',
          buttonText: '生成语音',
          emptyIcon: '🎵',
          emptyText: '输入文本后点击"生成语音"',
          emptyTip: 'AI将为您朗读文本内容'
        }
      }
    };
  },
  
  computed: {
    currentConfig() {
      return this.configs[this.activeTab];
    },
    
    canGenerate() {
      if (this.activeTab === 'textToVideo') {
        return this.formData.textToVideo.prompt.trim();
      } else if (this.activeTab === 'textToAudio') {
        return this.formData.textToAudio.text.trim();
      }
      // 图生视频和视频特效使用预设参数
      return true;
    }
  },
  
  mounted() {
    this.loadHistory();
  },
  
  methods: {
    handleTabClick(tab) {
      this.currentResult = null;
      this.errorMessage = '';
    },
    
    async generateContent() {
      this.isLoading = true;
      this.errorMessage = '';
      this.currentResult = null;
      
      try {
        let response;
        let resultData;
        
        if (this.activeTab === 'textToVideo') {
          response = await axios.get('/ai/texttovideo', {
            params: { message: this.formData.textToVideo.prompt }
          });
          resultData = typeof response.data === 'string' ? JSON.parse(response.data) : response.data;
          
          const videoUrl = resultData.output?.video_url || resultData.data?.url;
          if (videoUrl) {
            this.currentResult = {
              type: 'video',
              url: videoUrl,
              prompt: this.formData.textToVideo.prompt,
              time: this.formatTime(new Date())
            };
            this.addToHistory({
              type: 'video',
              typeName: '文字生成视频',
              typeIcon: 'el-icon-video-camera',
              prompt: this.formData.textToVideo.prompt,
              url: videoUrl,
              time: this.currentResult.time
            });
            this.$message.success('视频生成成功！');
          } else {
            throw new Error('未能获取到视频URL');
          }
          
        } else if (this.activeTab === 'imageToVideo') {
          response = await axios.get('/ai/imagetovide');
          resultData = typeof response.data === 'string' ? JSON.parse(response.data) : response.data;
          
          const videoUrl = resultData.output?.video_url || resultData.data?.url;
          if (videoUrl) {
            this.currentResult = {
              type: 'video',
              url: videoUrl,
              prompt: '图生视频（预设参数）',
              time: this.formatTime(new Date())
            };
            this.addToHistory({
              type: 'video',
              typeName: '图生视频',
              typeIcon: 'el-icon-picture-outline',
              prompt: '图生视频（预设参数）',
              url: videoUrl,
              time: this.currentResult.time
            });
            this.$message.success('视频生成成功！');
          } else {
            throw new Error('未能获取到视频URL');
          }
          
        } else if (this.activeTab === 'videoEffect') {
          response = await axios.get('/ai/videoeffect');
          resultData = typeof response.data === 'string' ? JSON.parse(response.data) : response.data;
          
          const videoUrl = resultData.output?.video_url || resultData.data?.url;
          if (videoUrl) {
            this.currentResult = {
              type: 'video',
              url: videoUrl,
              prompt: '视频特效（汉服特效）',
              time: this.formatTime(new Date())
            };
            this.addToHistory({
              type: 'video',
              typeName: '视频特效',
              typeIcon: 'el-icon-magic-stick',
              prompt: '视频特效（汉服特效）',
              url: videoUrl,
              time: this.currentResult.time
            });
            this.$message.success('特效视频生成成功！');
          } else {
            throw new Error('未能获取到视频URL');
          }
          
        } else if (this.activeTab === 'textToAudio') {
          response = await axios.get('/ai/texttoaudio', {
            params: { message: this.formData.textToAudio.text }
          });
          
          this.currentResult = {
            type: 'text',
            message: response.data,
            prompt: this.formData.textToAudio.text,
            time: this.formatTime(new Date())
          };
          this.addToHistory({
            type: 'audio',
            typeName: '文字转语音',
            typeIcon: 'el-icon-headset',
            prompt: this.formData.textToAudio.text.substring(0, 30) + '...',
            url: '/tts/output.mp3',
            time: this.currentResult.time
          });
          this.$message.success('语音生成成功！');
        }
        
      } catch (error) {
        console.error('生成失败:', error);
        this.errorMessage = error.message || '生成失败，请稍后重试';
        this.$message.error(this.errorMessage);
      } finally {
        this.isLoading = false;
      }
    },
    
    cancelGeneration() {
      this.isLoading = false;
      this.errorMessage = '已取消生成';
    },
    
    regenerate() {
      this.generateContent();
    },
    
    viewHistory(item) {
      if (item.type === 'video') {
        this.currentResult = {
          type: 'video',
          url: item.url,
          prompt: item.prompt,
          time: item.time
        };
      } else if (item.type === 'audio') {
        this.currentResult = {
          type: 'audio',
          url: item.url,
          prompt: item.prompt,
          time: item.time
        };
      }
    },
    
    addToHistory(item) {
      if (this.historyList.length >= 20) {
        this.historyList.pop();
      }
      this.historyList.unshift({ ...item });
      this.saveHistory();
    },
    
    clearHistory() {
      this.$confirm('确定要清空所有历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.historyList = [];
        localStorage.removeItem('aiMultimediaHistory');
        this.$message.success('已清空历史记录');
      }).catch(() => {});
    },
    
    saveHistory() {
      localStorage.setItem('aiMultimediaHistory', JSON.stringify(this.historyList));
    },
    
    loadHistory() {
      const history = localStorage.getItem('aiMultimediaHistory');
      if (history) {
        try {
          this.historyList = JSON.parse(history);
        } catch (e) {
          console.error('加载历史记录失败:', e);
        }
      }
    },
    
    handleMediaError() {
      this.errorMessage = '媒体文件加载失败，URL可能已过期';
    },
    
    formatTime(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      const seconds = String(date.getSeconds()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
    }
  }
};
</script>

<style scoped>
.ai-multimedia-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #87CEEB 0%, #4A90E2 50%, #1E90FF 100%);
  padding: 20px;
}

/* 头部样式 */
.header {
  max-width: 1400px;
  margin: 0 auto 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 20px;
  color: white;
}

.logo-placeholder {
  font-size: 48px;
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.25);
  border-radius: 20px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.title-container h1 {
  margin: 0 0 8px 0;
  font-size: 32px;
  font-weight: 700;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.2);
}

.subtitle {
  margin: 0;
  font-size: 16px;
  opacity: 0.95;
}

/* 标签页容器 */
.tabs-container {
  max-width: 1400px;
  margin: 0 auto 20px;
}

.tabs-container ::v-deep .el-tabs__item {
  background: rgba(255, 255, 255, 0.8);
  border: none;
  color: #1E90FF;
  font-weight: 600;
  transition: all 0.3s;
}

.tabs-container ::v-deep .el-tabs__item:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
}

.tabs-container ::v-deep .el-tabs__item.is-active {
  background: white;
  color: #1E90FF;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 主内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 450px 1fr;
  gap: 20px;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  overflow: hidden;
  margin-bottom: 20px;
  transition: transform 0.3s, box-shadow 0.3s;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.card-header {
  padding: 20px;
  background: linear-gradient(135deg, #4A90E2 0%, #1E90FF 100%);
  color: white;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 18px;
  font-weight: 600;
}

.card-body {
  padding: 20px;
}

/* 表单区域 */
.form-section {
  margin-bottom: 15px;
}

.quick-prompts {
  margin-top: 10px;
}

.quick-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
  font-weight: 600;
}

.prompt-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.prompt-chip {
  padding: 6px 12px;
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  color: #1976D2;
  border: 1px solid #90CAF9;
}

.prompt-chip:hover {
  background: linear-gradient(135deg, #4A90E2 0%, #1E90FF 100%);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(30, 144, 255, 0.3);
}

/* 按钮样式 */
.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.generate-btn, .cancel-btn {
  flex: 1;
  padding: 14px 24px;
  border: none;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.generate-btn {
  background: linear-gradient(135deg, #4A90E2 0%, #1E90FF 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(30, 144, 255, 0.3);
}

.generate-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(30, 144, 255, 0.4);
}

.generate-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.cancel-btn {
  background: #f0f0f0;
  color: #666;
}

.cancel-btn:hover {
  background: #e0e0e0;
}

/* 历史记录 */
.history-card {
  max-height: 400px;
  display: flex;
  flex-direction: column;
}

.clear-history-btn {
  margin-left: auto;
  background: transparent;
  border: none;
  color: white;
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
  transition: background 0.3s;
}

.clear-history-btn:hover {
  background: rgba(255, 255, 255, 0.2);
}

.history-list {
  overflow-y: auto;
  max-height: 320px;
}

.history-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 8px;
  border: 1px solid #E3F2FD;
}

.history-item:hover {
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  transform: translateX(4px);
}

.history-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #4A90E2 0%, #1E90FF 100%);
  border-radius: 8px;
  color: white;
  font-size: 18px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
  min-width: 0;
}

.history-type {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #4A90E2;
  font-weight: 600;
}

.history-prompt {
  margin: 0 0 5px 0;
  font-size: 13px;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.history-time {
  font-size: 12px;
  color: #999;
}

/* 结果展示区域 */
.result-card {
  min-height: 600px;
  display: flex;
  flex-direction: column;
}

.result-body {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.empty-state, .loading-state, .error-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 40px;
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
  opacity: 0.6;
}

.empty-state p {
  margin: 8px 0;
  color: #666;
  font-size: 16px;
}

.empty-tip {
  font-size: 14px;
  color: #999;
}

/* 加载状态 */
.loading-spinner {
  margin-bottom: 20px;
}

.spinner {
  width: 60px;
  height: 60px;
  border: 4px solid #E3F2FD;
  border-top-color: #1E90FF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.loading-state p {
  margin: 8px 0;
  color: #666;
  font-size: 16px;
}

.loading-tip {
  font-size: 14px;
  color: #999;
}

/* 结果展示 */
.result-display {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.video-result, .audio-result, .text-result {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.result-video {
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  background: #000;
}

.audio-icon {
  font-size: 60px;
  margin-bottom: 15px;
}

.audio-text {
  font-size: 14px;
  color: #666;
  margin-bottom: 15px;
  line-height: 1.6;
}

.result-audio {
  width: 100%;
  margin-bottom: 15px;
}

.success-icon {
  font-size: 60px;
  margin-bottom: 15px;
}

.success-message {
  font-size: 16px;
  color: #4CAF50;
  margin-bottom: 15px;
}

.result-info {
  margin-top: 15px;
  padding: 15px;
  background: linear-gradient(135deg, #E3F2FD 0%, #BBDEFB 100%);
  border-radius: 8px;
  border-left: 4px solid #1E90FF;
}

.result-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #333;
  line-height: 1.6;
}

.result-actions {
  margin-top: 15px;
  display: flex;
  gap: 10px;
}

.action-btn {
  flex: 1;
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-decoration: none;
}

.download-btn {
  background: linear-gradient(135deg, #4A90E2 0%, #1E90FF 100%);
  color: white;
}

.download-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(30, 144, 255, 0.4);
}

.regenerate-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.regenerate-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(118, 75, 162, 0.4);
}

/* 错误状态 */
.error-icon {
  font-size: 60px;
  margin-bottom: 20px;
}

.error-message {
  color: #f44336;
  font-size: 16px;
  margin-bottom: 20px;
}

.retry-btn {
  padding: 10px 24px;
  background: linear-gradient(135deg, #f44336 0%, #d32f2f 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.retry-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(244, 67, 54, 0.4);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .result-card {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .header {
    margin-bottom: 15px;
  }
  
  .logo-placeholder {
    width: 60px;
    height: 60px;
    font-size: 36px;
  }
  
  .title-container h1 {
    font-size: 24px;
  }
  
  .subtitle {
    font-size: 14px;
  }
  
  .main-content {
    gap: 15px;
  }
  
  .card-header {
    padding: 15px;
    font-size: 16px;
  }
  
  .card-body {
    padding: 15px;
  }
}
</style>
