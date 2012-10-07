package com.bubble.db.comment;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;



public class ArgumentGame {
	
	//Votes can affect comments indirectly, but support can't
	private static final float IMPACT_OF_VOTES_ON_ATTACK = (float) 0.3;
	private static final int DIRECT_CONTRIBUTION_OF_VOTES_ON_RANK = 6;
	private static final int IMPORTANCE_OF_SUPPORT = 15;
	
	Map<Long, CommentTree> commentTreeMap;
	long arguedComment;
	
	public ArgumentGame(Map<Long, CommentTree> commentTreeMap) {
		this.commentTreeMap = commentTreeMap;		
	}
	
	public double play() {
		Set<Long> ids = getRelevant();
		float[][] rewardMatrix = getRewardMatrix(ids);
		float[] c = new float[rewardMatrix[0].length];
		float[] b = new float[rewardMatrix.length];
		
		// The game theoretic problem amounts to maximizing Exi, s.t. Ax <= 1 
		Arrays.fill(c, 1);
		Arrays.fill(b, 1);
		float gameVal;
		try {
			Simplex lp = new Simplex(rewardMatrix, b, c);
			gameVal = (10/lp.value());
		} catch (Exception e) {
			return commentTreeMap.get(arguedComment).getComment().getRank();
		}
		if(gameVal > 7) gameVal = 7;
		double popOp = getPopularOpinion();
		double totalScore = trunc(gameVal + popOp);
		
				
		if(totalScore >= 10)
			return  10.0;
		else if(totalScore <= 0)
			return  0.1;
		else
			return totalScore; 
	}
	
    
    //keep only the comments that are in the same tree as the discussed comment
    public Set<Long> getRelevant() {
    	LinkedList<CommentTree> relevantNodesQueue = new LinkedList<CommentTree>();   
    	Set<Long> res = new HashSet<Long>();
    	long parentId = commentTreeMap.get(arguedComment).getComment().getParentId(); 
    	if(parentId != 0) {
    		relevantNodesQueue.add(commentTreeMap.get(parentId));
    	} else {
    		relevantNodesQueue.add(commentTreeMap.get(arguedComment));
    	}
    	while(!relevantNodesQueue.isEmpty()) {
    		CommentTree examined = relevantNodesQueue.getFirst();
    		res.add(examined.getComment().getId());
    		relevantNodesQueue.removeFirst();
    		if(!examined.getReplies().isEmpty())relevantNodesQueue.addAll(examined.getReplies());    		
    	}
    	return res;
    }
   
    private float getPopularOpinion() {
    	int posVot = commentTreeMap.get(arguedComment).getComment().getPositiveVotes();
    	int negVot = commentTreeMap.get(arguedComment).getComment().getNegativeVotes();
    	float supporters = 0;
    	
		List<CommentTree> directReplies = commentTreeMap.get(arguedComment).getReplies();
		Iterator<CommentTree> itr2 = directReplies.iterator();
		
		while(itr2.hasNext()) {
			Comment reply = itr2.next().getComment();
			if(reply.isAgreeing()) supporters++;
		}
    	
    	return (float) (((float) ((posVot - negVot)*DIRECT_CONTRIBUTION_OF_VOTES_ON_RANK
    						+ supporters*IMPORTANCE_OF_SUPPORT))*0.05);
    }
    
    static double trunc(double a) {
    	long y=	Math.round(a*10);
        return (double) y/10;
    }
	
	/* Compute reward for a single game between prop and opp,
	   given their attacks' and supports' strength */
	public float computeReward(Map<String, Float> stats) {
		if(stats.get("selfAttacking") != 0)
			return 0;
		else if(stats.get("oppAttacks") == 0) {
			return 1;
		} else {	
			return (float) (0.5*(1+f(stats.get("propAttacks")) - f(stats.get("oppAttacks"))));
		}
	}
	
	// Scaling attacks in range  [0,1]
	public static float f(float n) {	
		return n/(n+1);
	}
		
	public float[][] getRewardMatrix(Set<Long> nodes) {

		/* get all combinations of arguments. Each set of arguments is
		   equivalent to an opinion which a player may choose to play a game */
		Set<Set<Long>> allCombs = getCombinations(nodes);		
		Set<Set<Long>> propArgs = getProponentArgs(allCombs);
		
		float[][] result = new float[propArgs.size()][allCombs.size()];
		Iterator<Set<Long>> propArgsItr = propArgs.iterator();
		int i=0, j=0;
		while(propArgsItr.hasNext()) {
			Set<Long> propOpinion = propArgsItr.next();
			Iterator<Set<Long>> oppArgsItr = allCombs.iterator();
			while(oppArgsItr.hasNext()) {
				Set<Long> oppOpinion = oppArgsItr.next();
				result[i][j] = (float) computeReward(getStats(propOpinion, oppOpinion));
				j++;
			}
			j = 0;
			i++;			
		}

		return result;
	}	
	
	private Set<Set<Long>> getProponentArgs(Set<Set<Long>> allCombs) {
		Set<Set<Long>> res = new HashSet<Set<Long>>();
		Iterator<Set<Long>> itr1 = allCombs.iterator();
		while(itr1.hasNext()) {
			Set<Long> c = itr1.next();
			if(c.contains(arguedComment)) {				
				res.add(c);
			}
		}
		
		return res;
	}
	
	private Set<Set<Long>> getCombinations(Set<Long> nodes) {
		Long[] nodeArr = nodes.toArray(new Long[nodes.size()]);
		Set<Set<Long>> allCombs = new HashSet<Set<Long>>();
		for(int i = 1; i <= nodeArr.length; i++) {
			allCombs.addAll(combinationsOfSize(i, nodeArr));
		}
		
		return allCombs;
	}
	
	public static Set<Set<Long>> combinationsOfSize(int size, Long[] array){		
        int len = array.length;
        Long[] aux = new Long[size];
        Set<Set<Long>> combl = new HashSet<Set<Long>>();
        generate(array, aux, len, 0, 0, size, combl);
        return combl;
    }
    
    private static void generate(Long[] allElems, Long[] aux, int len, 
    							 int combLen, int start, int l,Set<Set<Long>> res) {
    	
        for(int i=start;i<len;i++){
            aux[combLen] = allElems[i];
            if(combLen == l-1) {
                res.add(new HashSet<Long>(Arrays.asList(aux)));
            } else {
                generate(allElems, aux, len, combLen+1, i+1, l, res);
            }        
        }
    }
    

    /* Get how strongly the players attack each other with the opinions 
       chosen in this game. Takes into account votes and support */
    public Map<String, Float> getStats(Set<Long> prop, Set<Long> opp) {
    	//measure opponent's attack strength
    	float propAttacks = (float) 0.0, oppAttacks = (float) 0.0;
    	boolean selfAttacking = false;
    	Comment reply;
    	CommentTree propArg, oppArg;
    	Iterator<CommentTree> replies;
    	Iterator<Long> itr1 = prop.iterator();
    	while(itr1.hasNext()) {
    		propArg = commentTreeMap.get(itr1.next());
    		replies = propArg.getReplies().iterator();
    		while(replies.hasNext()) {
    			reply = replies.next().getComment();    			
    			long id = reply.getId();
    			selfAttacking = prop.contains(id) && !reply.isAgreeing();
    			if(opp.contains(id) && !reply.isAgreeing()){
   					oppAttacks += weighCommentAttack(reply);  
    			}
    		}
    		if(selfAttacking) break;
    	}
    	
    	if(!selfAttacking) {
    	//measure proponent's attack strength
    	itr1 = opp.iterator();
    	while(itr1.hasNext()) {
    		oppArg = commentTreeMap.get(itr1.next());
    		replies = oppArg.getReplies().iterator();
    		while(replies.hasNext()) {
    			reply = replies.next().getComment();    			
    			long id = reply.getId();
    			if(prop.contains(id) && !reply.isAgreeing()) {
   					propAttacks += weighCommentAttack(reply);    					
    			}
    		}
    	}  
    	}
    	
    	Map<String, Float> stats = new HashMap<String, Float>();
    	stats.put("selfAttacking", (selfAttacking) ? (float) 1.0 : (float) 0.0);
    	stats.put("oppAttacks", (oppAttacks < 0) ? 0 : oppAttacks);
    	stats.put("propAttacks", (propAttacks < 0) ? 0 : propAttacks);
    	
    	return stats;
    }
    
	private float weighCommentAttack(Comment c) {
    	int posVot = c.getPositiveVotes();
    	int negVot = c.getNegativeVotes();
    	float strength = 1 + ((float) (posVot-negVot))*IMPACT_OF_VOTES_ON_ATTACK; 

    	return (strength < 0) ? 0 : strength; 
    }
	
	public void setArguedComment(long c) {
		this.arguedComment = c;
	}

}
