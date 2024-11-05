package com.application.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.application.dynamic.DataSourceInfo;
import com.application.dynamic.DataSourceUtils;
import com.application.dynamic.DynamicDataSourceHolder;
import com.application.mapper.CommonMapper;
import com.application.service.TerrainService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;

@Component
@Service

public class TerrainServiceImpl extends ServiceImpl<CommonMapper, String> implements TerrainService {
    @Autowired
    private CommonMapper commonMapper;
    @Resource
    DataSourceUtils dataSourceUtils;
    //@Autowired
    //private JdbcTemplate jdbcTemplate;
    @Value("${terrain.directory}")
    private String terrainDirectory;

    @Override
    //@DS("sqlite")
    public Object getTerrainType(String type,Integer z, Integer x, Integer y){
        String tableName = "";
        Object res = null;
        if(z < 10){
            tableName = "blocks";
        }else{
            tableName = "blocks_"+String.valueOf(z)+"_"+String.valueOf(x/512)+"_"+String.valueOf(y/512);
        }
        String sql = "select tile from "+tableName+" where z="+z+" and x="+x+" and y="+y+" limit 1";
        log.debug("sql:"+sql);

        
        //3、从数据库获取连接信息，然后获取数据
        //模拟从数据库中获取的连接
        DataSourceInfo dataSourceInfo = new DataSourceInfo(
                "jdbc:sqlite:"+terrainDirectory+type+".sqlite",
                "",
                "",
                type,
                "org.sqlite.JDBC");


        DruidDataSource druidDataSource = dataSourceUtils.findDataSource(dataSourceInfo.getDatasourceKey());

        if(druidDataSource == null){
            //测试数据源连接
            log.debug("this datasource is null");
            druidDataSource = dataSourceUtils.createDataSourceConnection(dataSourceInfo);
        }
        log.debug("this datasource is not null");
        if (Objects.nonNull(druidDataSource)){
            //将新的数据源连接添加到目标数据源map中
            dataSourceUtils.addDefineDynamicDataSource(druidDataSource,dataSourceInfo.getDatasourceKey());
            //设置当前线程数据源名称-----代码形式
            DynamicDataSourceHolder.setDynamicDataSourceKey(dataSourceInfo.getDatasourceKey());
            //在新的数据源中查询用户信息

            try{
                log.debug("query start:"+System.currentTimeMillis());
                res = commonMapper.querySql(sql);


            }catch(Exception e){
                e.printStackTrace();
            }
            log.debug("query end:"+System.currentTimeMillis());
            
            //关闭数据源连接
            //druidDataSource.close();
        }
        return res;

    }

}
