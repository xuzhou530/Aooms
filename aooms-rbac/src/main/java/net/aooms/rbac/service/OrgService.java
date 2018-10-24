package net.aooms.rbac.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import net.aooms.core.AoomsConstants;
import net.aooms.core.id.IDGenerator;
import net.aooms.core.module.mybatis.Db;
import net.aooms.core.module.mybatis.SqlPara;
import net.aooms.core.record.PagingRecord;
import net.aooms.core.record.Record;
import net.aooms.core.record.TreeRecord;
import net.aooms.core.service.GenericService;
import net.aooms.core.util.Kv;
import net.aooms.core.util.TreeUtils;
import net.aooms.rbac.mapper.RbacMapperPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.List;
import java.util.stream.IntStream;

/**
 * 机构管理
 * Created by 风象南(cheereebo) on 2018-10-08
 */
@Service
public class OrgService extends GenericService {

    @Autowired
    private Db db;

    @Transactional(readOnly = true)
    public void findList() {
        SqlPara sqlPara = SqlPara.fromDataBoss().paging();
        sqlPara.and("status","parent_org_id")
               .andLikeAfter("org_name","org_shortname","org_code");

        String statementId = getStatementId(RbacMapperPackage.class,"OrgMapper.findList");
		PagingRecord pagingRecord = db.findList(statementId,sqlPara);
		this.setResultValue(AoomsConstants.Result.DATA,pagingRecord);
	}

	@Transactional(readOnly = true)
	public void findTree() {
		String statementId = getStatementId(RbacMapperPackage.class,"OrgMapper.findList");
		PagingRecord pagingRecord = db.findList(statementId,SqlPara.SINGLETON);

        TreeUtils treeUtils = new TreeUtils(pagingRecord.getList());
        treeUtils.setParentIdKey("parent_org_id");
        //treeUtils.setConvertValueKey(Kv.fkv("org_name","label"));
        treeUtils.setDefaultValue(Kv.fkv("icon","el-icon-news"));
        List<Record> treeRecords = treeUtils.listTree(AoomsConstants.TREE_ROOT);

		this.setResultValue(AoomsConstants.Result.TREE, treeRecords);
	}

	@Transactional
	public void insert() {
		Record record = Record.empty();
		record.set(AoomsConstants.ID,IDGenerator.getStringValue());
		record.setByJsonKey("formData");
		record.set("create_time", DateUtil.now());
		record.set("org_permission", this.permissionCode(record.getString("parent_org_id")));
        record.set("org_level", this.orgLevel(record.getString("parent_org_id")) + 1);
        db.insert("aooms_rbac_org",record);

		// 返回前台
		//record.convertValueKey(Kv.fkv("org_name","label"),true);
		record.set("icon","el-icon-news");
		this.setResultValue(AoomsConstants.Result.RECORD, record);
	}

	@Transactional
	public void update() {
        Record record = Record.empty();
        record.setByJsonKey("formData");
        record.set("update_time",DateUtil.now());
        record.set("org_permission", this.permissionCode(record.getString("parent_org_id")));
        record.set("org_level", this.orgLevel(record.getString("parent_org_id")) + 1);
        db.update("aooms_rbac_org",record);

        //record.convertValueKey(Kv.fkv("org_name","label"),true);
        record.set("icon","el-icon-news");
        this.setResultValue(AoomsConstants.Result.RECORD, record);
    }

    @Transactional
    public void updateStatus() {
        Record record = Record.empty();
        record.set(AoomsConstants.ID, getParaString("id"));
        record.set("status", getParaString("status"));
        record.set("update_time",DateUtil.now());
        db.update("aooms_rbac_org",record);
    }

	@Transactional
	public void delete() {
        List<Object> ids = this.getListFromJson("ids",AoomsConstants.ID);
		db.batchDelete("aooms_rbac_org",ids.toArray());
	}

    /**
     *
     * 私有辅助
     *
     */

    // 生成permissionCode
    private String permissionCode(String parentId){
        int permissionLen = 4;
        //char permissionSplicChar = '.';
        String permission;

        synchronized (this.getClass()){
            String statementId = getStatementId(RbacMapperPackage.class,"OrgMapper.findMaxOrgPermission");
            Integer maxPermission = db.findObject(statementId,SqlPara.empty().set("parent_org_id",parentId),Integer.class);
            if(maxPermission == null){
                maxPermission = 0;
            }
            maxPermission += 1;

            permission = maxPermission + "";
            int start = permission.length();
            for(int index = start; index < permissionLen; index++){
                permission = "0" + permission;
            };
        }
     System.err.println("permission" + permission);
        return permission;
    }

    // 生成permissionCode
    private Integer orgLevel(String parentId){
        String statementId = getStatementId(RbacMapperPackage.class,"OrgMapper.findOrgLevel");
        Integer orgLevel = db.findObject(statementId,SqlPara.empty().set("parent_org_id",parentId),Integer.class);
        if(orgLevel == null){
            orgLevel = 0;
        }
        return orgLevel;
    }


}