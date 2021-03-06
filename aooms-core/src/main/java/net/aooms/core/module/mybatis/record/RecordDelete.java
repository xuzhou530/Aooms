package net.aooms.core.module.mybatis.record;

import net.aooms.core.AoomsVar;
import net.aooms.core.module.mybatis.MyBatisConst;
import net.aooms.core.module.mybatis.interceptor.MetaObjectAssistant;
import net.aooms.core.record.Record;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;

import java.util.Map;

/**
 * RecordDelete
 * Created by 风象南(yuboon) on 2018/9/7
 */
public class RecordDelete implements IRecordOper {

    private MetaObject metaObject;

    public RecordDelete(MetaObject metaObject) {
        this.metaObject = metaObject;
    }

    @Override
    public void process() {
        MappedStatement mappedStatement = MetaObjectAssistant.getMappedStatement(metaObject);
        Object parameterObject = MetaObjectAssistant.getParameterObject(metaObject);
        Record record = (Record) parameterObject;

        String tableName = record.getGeneral(MyBatisConst.TABLE_NAME_PLACEHOLDER);
        String pkName = record.getGeneralOrDefault(MyBatisConst.TABLE_PK_NAME_PLACEHOLDER, AoomsVar.ID);
        Object pkValue = record.get(pkName);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(" delete from ");
        stringBuilder.append(tableName); // tableName
        stringBuilder.append(" where "+ pkName +" = #{"+ pkName +"} ");
        String sql = stringBuilder.toString();

       // SqlSource sqlSource = new XMLLanguageDriver().createSqlSource(mappedStatement.getConfiguration(), sql, Map.class);
        Configuration configuration = MetaObjectAssistant.getConfiguration(metaObject);
        SqlSource sqlSource = configuration.getLanguageRegistry().getDefaultDriver().createSqlSource(mappedStatement.getConfiguration(), sql, Map.class);
        BoundSql boundSql = sqlSource.getBoundSql(parameterObject);

        MetaObjectAssistant.setDelegateBoundSql(metaObject,boundSql);
        MetaObjectAssistant.setDelegateParameterHandlerBoundSql(metaObject,boundSql);
    }

}
