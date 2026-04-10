<template>
  <div class="app-container">

    <!--查询表单-->
    <div class="search-div">
      <el-form label-width="70px" size="small">
        <el-row>
          <el-col :span="24">
            <el-form-item label="用户名">
              <el-input style="width: 100%" v-model="searchObj.username" placeholder="用户名"></el-input>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row style="display:flex">
          <el-button type="primary" icon="el-icon-search" size="mini"  @click="fetchData()">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetData">重置</el-button>
        </el-row>
      </el-form>
    </div>

    <!-- 工具条 -->
    <div class="tools-div">
      <el-button type="success" icon="el-icon-plus" size="mini" @click="add">添 加</el-button>
      <el-button class="btn-add" size="mini" @click="batchRemove()">批量删除</el-button>
    </div>

    <!-- 表格 -->
    <el-table
      v-loading="listLoading"
      :data="list"
      stripe
      border
      style="width: 100%;margin-top: 10px;"
      @selection-change="handleSelectionChange">
      <el-table-column type="selection"/>

      <el-table-column
        label="序号"
        width="70"
        align="center">
        <template slot-scope="scope">
          {{ (page - 1) * limit + scope.$index + 1 }}
        </template>
      </el-table-column>

      <el-table-column prop="username" label="用户名" />
      <el-table-column prop="name" label="姓名" />
      <el-table-column prop="phone" label="手机号" />
      <el-table-column prop="email" label="邮箱" />
      <el-table-column label="性别" width="80">
        <template slot-scope="scope">
          <span v-if="scope.row.gender === 1">男</span>
          <span v-else-if="scope.row.gender === 2">女</span>
          <span v-else>未知</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === 0" type="success">正常</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160"/>
      <el-table-column label="操作" width="200" align="center">
        <template slot-scope="scope">
          <el-button type="primary" icon="el-icon-edit" size="mini" @click="edit(scope.row.id)" title="修改"/>
          <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeDataById(scope.row.id)" title="删除"/>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页组件 -->
    <el-pagination
      :current-page="page"
      :total="total"
      :page-size="limit"
      style="padding: 30px 0; text-align: center;"
      layout="total, prev, pager, next, jumper"
      @current-change="fetchData"
    />

    <!-- 弹框 -->
    <el-dialog title="添加/修改" :visible.sync="dialogVisible" width="50%" >
      <el-form ref="dataForm" :model="sysUser" label-width="100px" size="small" style="padding-right: 40px;">
        <el-form-item label="用户名">
          <el-input v-model="sysUser.username" :disabled="sysUser.id != null"/>
        </el-form-item>
        <el-form-item label="密码" v-if="!sysUser.id">
          <el-input v-model="sysUser.password" type="password" show-password/>
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="sysUser.name"/>
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="sysUser.phone"/>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="sysUser.email"/>
        </el-form-item>
        <el-form-item label="性别">
          <el-radio-group v-model="sysUser.gender">
            <el-radio :label="0">未知</el-radio>
            <el-radio :label="1">男</el-radio>
            <el-radio :label="2">女</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="sysUser.status">
            <el-radio :label="0">正常</el-radio>
            <el-radio :label="1">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false" size="small" icon="el-icon-refresh-right">取 消</el-button>
        <el-button type="primary" icon="el-icon-check" @click="saveOrUpdate()" size="small">确 定</el-button>
      </span>
    </el-dialog>

  </div>
</template>

<script>
import api from '@/api/user/user.js'
export default {
  data () {
    return {
      list:[],
      total:0,
      page:1,
      limit:10,
      searchObj:{},
      // 封装表单中 user 数据 
      sysUser:{},
      dialogVisible:false,  // 控制弹框的默认隐藏
      // 选中的数据
      selectValueData:[]
    }
  },
  created () {
    this.fetchData();
  },
  methods: {
    fetchData(pageNum=1){
      this.page = pageNum;
      api.getPageList(this.page, this.limit, this.searchObj.username).then(response=>{
        this.list = response.data.records;
        this.total = response.data.total;
      })
    },
    resetData(){
      // 清空表单
      this.searchObj={};
      // 再次刷新列表数据
      this.fetchData();
    },
    // 打开弹框
    add(){
      this.dialogVisible = true;
      this.sysUser={ gender: 0, status: 0 };
    },
    // 删除操作
    removeDataById(id) {
      this.$confirm("此操作将永久删除该用户，是否继续？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        api.removeById(id)
          .then((response) => {
            // 提示
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            // 刷新页面
            this.fetchData();
          });
      });
    },
    // 根据 id 回显数据
    edit(id){
      this.dialogVisible=true;
      api.getUserById(id)
      .then(response=>{
        this.sysUser = response.data;
      })
    },
    // 添加或者修改
    saveOrUpdate(){
      if(this.sysUser.id!=null){
        this.updateUser(); 
      } else {
        this.addUser();
      }
    },
    // 实现添加功能
    addUser(){
      api.saveUser(this.sysUser)
      .then(response=>{
        this.$message({
          type: "success",
          message: "添加成功!",
        }); 
        // 关闭弹框
        this.dialogVisible=false;
        // 刷新数据
        this.fetchData();
      })
    },
    // 实现修改功能
    updateUser(){
      api.updateUser(this.sysUser)
      .then(response=>{
        this.$message({
          type: "success",
          message: "修改成功!",
        }); 
        // 关闭弹框
        this.dialogVisible=false;
        // 刷新数据
        this.fetchData();
      })
    },
    // 获取选中的数据
    handleSelectionChange(selectValue){
      this.selectValueData = selectValue;
    },
    // 批量删除
    batchRemove(){
      // 判断是否有选中
      if(this.selectValueData.length==0){
         this.$message.warning('请选择要删除的记录！')
        return;
      }
      this.$confirm("此操作将永久删除选中的用户，是否继续？", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      }).then(() => {
        var ids=[];
        for(var i=0;i<this.selectValueData.length;i++){
          var obj= this.selectValueData[i];
          var id = obj.id;
          ids.push(id);
        }
        api.batchRemoveIds(ids)
          .then((response) => {
            // 提示
            this.$message({
              type: "success",
              message: "删除成功!",
            });
            // 刷新页面
            this.fetchData();
          });
      });
    },
  }
}
</script>

<style>
  .search-div {
    padding:10px;border: 1px solid #EBEEF5;border-radius:3px;
  }
  .tools-div {
    margin-top: 10px;padding:10px;border: 1px solid #EBEEF5;border-radius:3px;
  }
</style>
