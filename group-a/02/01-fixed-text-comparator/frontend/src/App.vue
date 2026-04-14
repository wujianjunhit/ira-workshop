<template>
  <div class="app">
    <el-container>
      <el-header>
        <h1>定长文本文件对比工具</h1>
      </el-header>
      <el-main>
        <FileComparator />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import FileComparator from './components/FileComparator.vue'
</script>

<style>
.app {
  min-height: 100vh;
  background: #f5f7fa;
}

.el-header {
  background: #409eff;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
}

.el-header h1 {
  margin: 0;
}
</style>
