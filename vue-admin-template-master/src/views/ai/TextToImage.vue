<template>
  <div class="text-to-image-container">
    <!-- 页面标题 -->
    <div class="header">
      <div class="logo-container">
        <div class="logo-placeholder">🎨</div>
        <div class="title-container">
          <h1 class="main-title">AI文字生成图片</h1>
          <p class="subtitle">输入描述，AI为您创作精美图片</p>
        </div>
      </div>
    </div>

    <!-- 主内容区域 -->
    <div class="main-content">
      <!-- 左侧：输入区域 -->
      <div class="input-section">
        <div class="card">
          <div class="card-header">
            <i class="icon-input"></i>
            <span>图片描述</span>
          </div>
          <div class="card-body">
            <textarea
              v-model="promptText"
              placeholder="请输入图片描述，例如：美丽的海滩日落、可爱的卡通猫咪、未来科技城市..."
              rows="6"
              class="prompt-input"
              :disabled="isLoading"
            ></textarea>
            
            <div class="quick-prompts">
              <p class="quick-title">快速选择：</p>
              <div class="prompt-chips">
                <span 
                  v-for="(prompt, index) in quickPrompts" 
                  :key="index" 
                  class="prompt-chip"
                  @click="useQuickPrompt(prompt)"
                >
                  {{ prompt }}
                </span>
              </div>
            </div>

            <div class="action-buttons">
              <button 
                class="generate-btn" 
                @click="generateImage"
                :disabled="!promptText.trim() || isLoading"
                :class="{ disabled: !promptText.trim() || isLoading }"
              >
                <i class="icon-generate"></i>
                <span v-if="isLoading">生成中...</span>
                <span v-else>生成图片</span>
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

        <!-- 生成历史 -->
        <div class="card history-card" v-if="historyList.length > 0">
          <div class="card-header">
            <i class="icon-history"></i>
            <span>生成历史</span>
            <button class="clear-history-btn" @click="clearHistory" title="清空历史">
              <i class="icon-clear"></i>
            </button>
          </div>
          <div class="card-body history-list">
            <div 
              v-for="(item, index) in historyList" 
              :key="index" 
              class="history-item"
              @click="viewHistory(item)"
            >
              <img :src="item.imageUrl" :alt="item.prompt" class="history-thumb" />
              <div class="history-info">
                <p class="history-prompt">{{ item.prompt }}</p>
                <span class="history-time">{{ item.time }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧：图片展示区域 -->
      <div class="image-section">
        <div class="card image-card">
          <div class="card-header">
            <i class="icon-image"></i>
            <span>生成结果</span>
          </div>
          <div class="card-body image-body">
            <!-- 初始状态 -->
            <div v-if="!currentImage && !isLoading" class="empty-state">
              <div class="empty-icon">🖼️</div>
              <p>输入描述后点击"生成图片"</p>
              <p class="empty-tip">AI将根据您的描述创作独特图片</p>
            </div>

            <!-- 加载状态 -->
            <div v-if="isLoading" class="loading-state">
              <div class="loading-spinner">
                <div class="spinner"></div>
              </div>
              <p>AI正在创作中，请稍候...</p>
              <p class="loading-tip">通常需要10-30秒</p>
            </div>

            <!-- 图片展示 -->
            <div v-if="currentImage && !isLoading" class="image-display">
              <div class="image-wrapper">
                <img 
                  :src="currentImage.url" 
                  :alt="currentImage.prompt"
                  class="generated-image"
                  @error="handleImageError"
                />
              </div>
              
              <div class="image-info">
                <p class="image-prompt">
                  <strong>描述：</strong>{{ currentImage.prompt }}
                </p>
                <p class="image-time">
                  <strong>生成时间：</strong>{{ currentImage.time }}
                </p>
              </div>

              <div class="image-actions">
                <button class="action-btn download-btn" @click="downloadImage">
                  <i class="icon-download"></i> 下载图片
                </button>
                <button class="action-btn regenerate-btn" @click="regenerateImage">
                  <i class="icon-regenerate"></i> 重新生成
                </button>
              </div>
            </div>

            <!-- 错误状态 -->
            <div v-if="errorMessage" class="error-state">
              <div class="error-icon">❌</div>
              <p class="error-message">{{ errorMessage }}</p>
              <button class="retry-btn" @click="generateImage">
                <i class="icon-retry"></i> 重试
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
  name: 'TextToImage',
  
  data() {
    return {
      promptText: '',
      currentImage: null,
      isLoading: false,
      errorMessage: '',
      historyList: [],
      quickPrompts: [
        '美丽的女孩在海边',
        '可爱的卡通猫咪',
        '未来科技城市夜景',
        '春天的樱花树',
        '雪山湖泊风光',
        '太空宇航员',
        '古风建筑庭院',
        '热带雨林瀑布'
      ]
    };
  },
  
  mounted() {
    // 从localStorage加载历史记录
    this.loadHistory();
  },
  
  methods: {
    // 使用快速提示词
    useQuickPrompt(prompt) {
      this.promptText = prompt;
    },
    
    // 生成图片
    async generateImage() {
      if (!this.promptText.trim()) {
        this.$message.warning('请输入图片描述');
        return;
      }
      
      this.isLoading = true;
      this.errorMessage = '';
      this.currentImage = null;
      
      try {
        const response = await axios.get('/ai/texttoimage', {
          params: {
            message: this.promptText
          }
        });
        
        console.log('API响应:', response.data);
        
        // 解析返回的JSON数据
        const result = typeof response.data === 'string' 
          ? JSON.parse(response.data) 
          : response.data;
        
        // 提取图片URL（根据阿里云API返回格式）
        let imageUrl = '';
        if (result.output && result.output.results && result.output.results.length > 0) {
          imageUrl = result.output.results[0].url;
        } else if (result.data && result.data.url) {
          imageUrl = result.data.url;
        }
        
        if (imageUrl) {
          this.currentImage = {
            url: imageUrl,
            prompt: this.promptText,
            time: this.formatTime(new Date())
          };
          
          // 添加到历史记录
          this.addToHistory(this.currentImage);
          
          this.$message.success('图片生成成功！');
        } else {
          throw new Error('未能获取到图片URL');
        }
        
      } catch (error) {
        console.error('生成图片失败:', error);
        this.errorMessage = error.message || '图片生成失败，请稍后重试';
        this.$message.error(this.errorMessage);
      } finally {
        this.isLoading = false;
      }
    },
    
    // 取消生成
    cancelGeneration() {
      this.isLoading = false;
      this.errorMessage = '已取消生成';
    },
    
    // 重新生成
    regenerateImage() {
      this.generateImage();
    },
    
    // 下载图片
    downloadImage() {
      if (!this.currentImage || !this.currentImage.url) return;
      
      const link = document.createElement('a');
      link.href = this.currentImage.url;
      link.download = `ai-image-${Date.now()}.png`;
      link.target = '_blank';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      this.$message.success('开始下载图片');
    },
    
    // 查看历史记录
    viewHistory(item) {
      this.currentImage = item;
      this.promptText = item.prompt;
    },
    
    // 添加到历史记录
    addToHistory(item) {
      // 最多保存20条记录
      if (this.historyList.length >= 20) {
        this.historyList.pop();
      }
      this.historyList.unshift({ ...item });
      this.saveHistory();
    },
    
    // 清空历史记录
    clearHistory() {
      this.$confirm('确定要清空所有历史记录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.historyList = [];
        localStorage.removeItem('textToImageHistory');
        this.$message.success('已清空历史记录');
      }).catch(() => {});
    },
    
    // 保存历史记录到localStorage
    saveHistory() {
      localStorage.setItem('textToImageHistory', JSON.stringify(this.historyList));
    },
    
    // 从localStorage加载历史记录
    loadHistory() {
      const history = localStorage.getItem('textToImageHistory');
      if (history) {
        try {
          this.historyList = JSON.parse(history);
        } catch (e) {
          console.error('加载历史记录失败:', e);
        }
      }
    },
    
    // 处理图片加载错误
    handleImageError() {
      this.errorMessage = '图片加载失败，URL可能已过期';
    },
    
    // 格式化时间
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
.text-to-image-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
}

/* 头部样式 */
.header {
  max-width: 1400px;
  margin: 0 auto 30px;
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
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  backdrop-filter: blur(10px);
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
  opacity: 0.9;
}

/* 主内容区域 */
.main-content {
  max-width: 1400px;
  margin: 0 auto;
  display: grid;
  grid-template-columns: 400px 1fr;
  gap: 20px;
}

/* 卡片样式 */
.card {
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  margin-bottom: 20px;
}

.card-header {
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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

/* 输入区域 */
.prompt-input {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  resize: vertical;
  transition: border-color 0.3s;
  font-family: inherit;
}

.prompt-input:focus {
  outline: none;
  border-color: #667eea;
}

.prompt-input:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

/* 快速提示词 */
.quick-prompts {
  margin-top: 15px;
}

.quick-title {
  font-size: 14px;
  color: #666;
  margin-bottom: 10px;
}

.prompt-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.prompt-chip {
  padding: 6px 12px;
  background: #f0f0f0;
  border-radius: 20px;
  font-size: 13px;
  cursor: pointer;
  transition: all 0.3s;
  color: #333;
}

.prompt-chip:hover {
  background: #667eea;
  color: white;
  transform: translateY(-2px);
}

/* 按钮样式 */
.action-buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

.generate-btn, .cancel-btn {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.generate-btn:hover:not(.disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
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
  transition: background 0.3s;
  margin-bottom: 8px;
}

.history-item:hover {
  background: #f5f5f5;
}

.history-thumb {
  width: 60px;
  height: 60px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.history-info {
  flex: 1;
  min-width: 0;
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

/* 图片展示区域 */
.image-card {
  min-height: 600px;
  display: flex;
  flex-direction: column;
}

.image-body {
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
  opacity: 0.5;
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
  border: 4px solid #f0f0f0;
  border-top-color: #667eea;
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

/* 图片展示 */
.image-display {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.image-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
  min-height: 400px;
}

.generated-image {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.image-info {
  margin-top: 20px;
  padding: 15px;
  background: #f9f9f9;
  border-radius: 8px;
}

.image-info p {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.image-actions {
  margin-top: 20px;
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
}

.download-btn {
  background: #667eea;
  color: white;
}

.download-btn:hover {
  background: #5568d3;
  transform: translateY(-2px);
}

.regenerate-btn {
  background: #764ba2;
  color: white;
}

.regenerate-btn:hover {
  background: #653d8f;
  transform: translateY(-2px);
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
  background: #f44336;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.retry-btn:hover {
  background: #d32f2f;
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
  
  .image-card {
    min-height: 400px;
  }
}

@media (max-width: 768px) {
  .header {
    margin-bottom: 20px;
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
