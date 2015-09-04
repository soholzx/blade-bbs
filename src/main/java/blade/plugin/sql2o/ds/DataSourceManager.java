package blade.plugin.sql2o.ds;

import javax.sql.DataSource;

import org.sql2o.Sql2o;

import blade.plugin.sql2o.Sql2oPlugin;
import blade.plugin.sql2o.exception.PoolException;
import blade.plugin.sql2o.pool.ConnectionPool;
import blade.plugin.sql2o.pool.ConnectionPoolManager;
import blade.plugin.sql2o.pool.InitPoolConfig;
import blade.plugin.sql2o.pool.PoolConfig;

/**
 * 数据源连接管理器
 * @author biezhi
 * @version 1.0
 */
public final class DataSourceManager {

	private static final DataSourceManager DATA_SOURCE_MANAGER = new DataSourceManager();
	
	private DataSource dataSource;
	
	private ConnectionPool connectionPool;
	
	private Sql2o sql2o = null;
	
	private DataSourceManager() {
	}
	
	public static DataSourceManager me(){
		return DATA_SOURCE_MANAGER;
	}
	
	public void run(){
		if(null != this.dataSource){
			sql2o = new Sql2o(this.dataSource);
		}
		if(null != this.connectionPool){
			sql2o = new Sql2o(connectionPool);
		}
	}
	
	public Sql2o getSql2o(){
		return sql2o;
	}
	
	public void setConnectionPool(ConnectionPool connectionPool) {
		if(null == connectionPool){
			this.connectionPool = connectionPool;
		}
	}
	
	public void setConnectionPool(PoolConfig poolConfig) {
		if(null == connectionPool){
			if(null == poolConfig){
				throw new PoolException("数据库配置失败");
			}
			InitPoolConfig.add(poolConfig);
			this.connectionPool = ConnectionPoolManager.me().getPool(poolConfig.getPoolName());
		}
	}
	
	public ConnectionPool getConnectionPool() {
		if(null == this.connectionPool){
			PoolConfig poolConfig = Sql2oPlugin.INSTANCE.poolConfig();
			if(null != poolConfig){
				InitPoolConfig.add(poolConfig);
				this.connectionPool = ConnectionPoolManager.me().getPool(poolConfig.getPoolName());
			}
		}
		return this.connectionPool;
	}

	/**
	 * 提供动态注入datasource
	 * @param dataSource_
	 */
	public void setDataSource(DataSource dataSource){
		this.dataSource = dataSource;
		if(null != this.dataSource){
			sql2o = new Sql2o(this.dataSource);
		}
	}
	
	public DataSource getDataSource(){
		return dataSource;
	}
	
}
