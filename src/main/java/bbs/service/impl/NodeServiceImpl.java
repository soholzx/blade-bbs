package bbs.service.impl;

import java.util.List;

import bbs.model.Node;
import bbs.service.NodeService;
import blade.annotation.Component;
import blade.kit.StringKit;
import blade.plugin.sql2o.Model;

@Component
public class NodeServiceImpl implements NodeService {
	
	private Model<Node> model = new Model<Node>(Node.class);
	
	@Override
	public List<Node> getNodes(Integer featured, Integer topshow) {
		List<Node> nodes = this.getNomalNodes(featured, topshow);
		if(null != nodes && nodes.size() > 0){
			for(Node node : nodes){
				if(node.getPid() == 0){
					List<Node> children = model.select().eq("pid", node.getNid()).eq("topshow", topshow).orderBy("nid desc").fetchList();
					if(null != children && children.size() > 0){
						node.setChildren(children);
					} else {
						
					}
				}
			}
		}
		return nodes;
	}

	@Override
	public List<Node> getNomalNodes(Integer featured, Integer topshow) {
		return model.select().eq("featured", featured)
				.eq("topshow", topshow).orderBy("nid desc").fetchList();
	}

	@Override
	public boolean save(String nname, String nkey, Integer pid, Integer featured,
			Integer topshow, String keywords, String content) {
		return model.insert()
				.param("nname", nname)
				.param("nkey", nkey)
				.param("pid", pid)
				.param("featured", featured)
				.param("topshow", topshow)
				.param("content", content)
				.param("keywords", keywords).executeAndCommit() > 0;
	}

	@Override
	public Node getNodeByNid(Integer nid) {
		return model.fetchByPk(nid);
	}

	@Override
	public Node getNodeByNKey(String nkey) {
		return model.select().eq("nkey", nkey).fetchOne();
	}

	@Override
	public boolean update(Integer nid, String nname, String nkey, Integer pid,
			Integer featured, Integer topshow, String keywords, String content) {
		if(null != nid && StringKit.isNotBlank(nname) && StringKit.isNotBlank(nkey) &&
				null != pid && null != featured && null != topshow){
			
			return model.update().param("nname", nname).param("nkey", nkey)
			.param("pid", pid).param("featured", featured).param("topshow", topshow)
			.param("keywords", keywords).param("content", content).eq("nid", nid).executeAndCommit() > 0;
		}
		return false;
	}
}
