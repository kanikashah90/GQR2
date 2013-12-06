package gqr;

import java.util.ArrayList;
import java.util.List;

class Infobox{

	private List<JoinInView> joinInViews = new ArrayList<JoinInView>();

	List<JoinInView> getJoinInViews() {
		return joinInViews;
	}

	void setJoinInViews(List<JoinInView> joinInViews) {
		this.joinInViews = joinInViews;
	}

	void addJoinInView(JoinInView joiv) {
		joinInViews.add(joiv);
	}
	
	@Override
	public String toString() {
		String ret = new String();
		for(JoinInView jv:joinInViews)
		{
			ret  += jv.toString()+"\n";
		}
		return ret;
	}
	
	@Override
	protected Infobox clone() throws CloneNotSupportedException {
		Infobox i = new Infobox();
		
		List<JoinInView> joins = new ArrayList<JoinInView>();
		for(JoinInView jv: this.getJoinInViews())
		{
			joins.add(jv.clone());
		}
		i.setJoinInViews(joins);
		return i;
	}
}
