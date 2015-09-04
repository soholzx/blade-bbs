package bbs.model;

import java.io.Serializable;

import blade.plugin.sql2o.Table;

@Table(value = "bbs_option", PK = "opt_key")
public class Option implements Serializable {

	private static final long serialVersionUID = -7718091985540738974L;

	private String opt_key;
	private String opt_value;

	public Option() {
		// TODO Auto-generated constructor stub
	}

	public String getOpt_key() {
		return opt_key;
	}

	public void setOpt_key(String opt_key) {
		this.opt_key = opt_key;
	}

	public String getOpt_value() {
		return opt_value;
	}

	public void setOpt_value(String opt_value) {
		this.opt_value = opt_value;
	}

}
