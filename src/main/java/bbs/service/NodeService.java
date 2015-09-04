package bbs.service;

import java.util.List;

import bbs.model.Node;

public interface NodeService {
	
	public Node getNodeByNid(Integer nid);
	
	public Node getNodeByNKey(String nkey);
	
	public List<Node> getNodes(Integer featured, Integer topshow);
	
	public List<Node> getNomalNodes(Integer featured, Integer topshow);
	
	public boolean save(String nname, String nkey, Integer pid, Integer featured, Integer topshow, String keywords, String content);

	public boolean update(Integer nid, String nname, String nkey, Integer pid, Integer featured, Integer topshow, String keywords, String content);
}
