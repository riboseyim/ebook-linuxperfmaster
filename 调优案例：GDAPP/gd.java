package com.ctsi.common;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserAuthManager
{
  public static final int ORACLE = 0;
  public static final int SYBASE = 1;
  public static final String DB_TYPE_ORACLE = "oracle";
  public static final String DB_TYPE_SYBASE = "sybase";
  private static UserAuthManager instance = null;

  private static int dbType = 0;

  private BaseBean oBaseBean = new BaseBean(???.getClass().getName());

  private String resclass2ends = "";
  private Connection conn;
  private boolean tagUpdated = false;

  private boolean isrunning = false;

  public static UserAuthManager getInstance()
  {
    if (instance == null)
    {
      instance = new UserAuthManager();
    }
    return instance;
  }

  private void confirmDbType() throws Exception
  {
    String url = this.conn.getMetaData().getURL();

    logHigh("confirmDbType is called :" + url);

    if (url.indexOf("oracle") != -1)
    {
      dbType = 0;
    } else {
      if (url.indexOf("sybase") == -1)
        return;
      dbType = 1;
    }
  }

  private void logHigh(Object o)
  {
    if (!(this.oBaseBean.bHighLevelLog))
      return;
    this.oBaseBean.debugLog(String.valueOf(o));
  }

  private void logLow(Object o)
  {
    if (!(this.oBaseBean.bLowLevelLog))
      return;
    this.oBaseBean.debugLog(String.valueOf(o));
  }

  private String getSysdate()
    throws Exception
  {
    String sql = "SELECT TO_CHAR(SYSDATE,'MM-dd-yyyy hh24:mi:ss') sTime FROM Dual";

    switch (dbType)
    {
    case 0:
      sql = "SELECT TO_CHAR(SYSDATE,'MM-dd-yyyy hh24:mi:ss') sTime FROM Dual";
      break;
    case 1:
      sql = "SELECT CONVERT(VARCHAR,GETDATE(),110)||' '||CONVERT(VARCHAR,GETDATE(),108) SysDate";
    }

    List l = executeQuery(sql, 1);
    String[] ss = (String[])l.get(0);
    return ss[0];
  }

  public int updateUserAuth(String netUserId, String resClassId, Connection con)
    throws Exception
  {
    if ((netUserId == null) || (netUserId.trim().equals("")))
    {
      return 0;
    }
    if (!(this.isrunning))
    {
      return updateUserAuth2(netUserId, resClassId, con);
    }

    new WaitThread(netUserId, resClassId, con).start();

    return 0;
  }

  public synchronized int updateUserAuth2(String netUserId, String resClassId, Connection con)
    throws Exception
  {
    this.isrunning = true;
    logLow("updateUserAuth is called:NetUserId=" + netUserId + 
      ";resclassid=" + resClassId);
    boolean innerConn = false;
    int result = 0;
    try
    {
      if (con == null)
      {
        con = this.oBaseBean.getConnection("jdbc/nmp_sh");
        innerConn = true;
      }

      this.conn = con;

      confirmDbType();

      this.tagUpdated = false;

      List resClasses = getResClass(resClassId);

      getResClass2Ends();

      if (isUserUpdated(netUserId))
      {
        this.tagUpdated = true;

        result += updateUserNodeAuth(netUserId, false);

        this.conn.commit();

        result = result + 
          updateUserResAuthExtend(netUserId, 
          getResClass("ALL"), false);

        this.conn.commit();

        result += updateUserTagAuth(netUserId, "ALL", true);

        updateUserAuthTime(netUserId);

        this.conn.commit(); break label451:
      }

      String sql = "SELECT r.resclassid  from resupdate r,userresupdate u where r.resclassid=u.resclassid and u.resclassid='TAG' and u.netuserid='" + 
        netUserId + "' ";
      if (dbType == 0)
      {
        sql = sql + " and TO_CHAR(r.updatetime,'MM-dd-yyyy hh24:mi:ss')=TO_CHAR(u.updatetime,'MM-dd-yyyy hh24:mi:ss') ";
      }
      else
      {
        sql = sql + " and CONVERT(VARCHAR,r.updatetime,110)||' '||CONVERT(VARCHAR,r.updatetime,108)=CONVERT(VARCHAR,u.updatetime,110)||' '||CONVERT(VARCHAR,u.updatetime,108) ";
      }
      List ll = executeQuery(sql, 1);
      if ((ll == null) || (ll.size() != 1))
      {
        this.tagUpdated = true;
      }

      result += updateUserNodeAuth(netUserId, true);

      this.conn.commit();

      result += updateUserResAuthExtend(netUserId, resClasses, true);

      this.conn.commit();

      if (this.tagUpdated)
      {
        result += updateUserTagAuth(netUserId, "ALL", true);
      }

      label451: this.conn.commit();
    }
    catch (Exception daoE)
    {
      logLow("An Exception has occured:" + daoE.getMessage());
      daoE.printStackTrace();
      this.conn.rollback();
    }
    finally
    {
      this.isrunning = false;
      if (innerConn)
      {
        this.oBaseBean.cleanCon(this.conn);
      }
      this.conn = null;
    }
    return result;
  }

  private void getResClass2Ends()
    throws Exception
  {
    this.resclass2ends = "";
    String substr = (dbType == 0) ? "substr" : "substring";
    String sql = "select distinct(" + substr + "(t.resid,1,3)) resclass from res t where t.nodecodeb is not null";
    List l = executeQuery(sql, 1);
    for (int i = 0; i < l.size(); ++i)
    {
      String[] ss = (String[])l.get(i);
      UserAuthManager tmp69_68 = this; tmp69_68.resclass2ends = tmp69_68.resclass2ends + "," + ss[0];
    }
  }

  private int updateUserTagAuth(String userId, String resClass, boolean updateTime)
    throws Exception
  {
    String sql;
    String substr = (dbType == 0) ? "substr" : "substring";

    if (resClass.equals("ALL"))
    {
      executeUpdate("DELETE FROM SingleUserResAuth WHERE NetUserID='" + 
        userId + "' AND TagFlag='TAG'");

      sql = "INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID,TagFlag) select distinct otherres.netuserid,otherres.resid,otherres.authtype,otherres.resclassid,otherres.tagflag from (select t.netuserid,r.resid,t.authtype," + 
        substr + "(r.restypeid,1,3) resclassid,'TAG' TagFlag " + 
        "from userresauth t,node n,res r,restag tag where t.restypeid='TAG' and t.netuserid='" + userId + "' and n.nodefullcode LIKE '%'||t.nodecode||'%' " + 
        "and (r.nodecodea=n.nodecode or r.nodecodeb=n.nodecode) and r.resid=tag.resid and tag.tag=t.resid) otherres " + 
        "LEFT OUTER JOIN SingleUserResAuth src ON otherres.NetUserId=src.NetUserId AND otherres.ResId=src.ResId AND otherres.AuthType=src.AuthType " + 
        "WHERE src.NetUserId is null AND src.ResId is null AND src.AuthType is null";

      executeUpdate(sql);
    }
    else
    {
      executeUpdate("DELETE FROM SingleUserResAuth WHERE NetUserID='" + 
        userId + "' AND TagFlag='TAG' AND ResClassID='" + resClass + "'");

      sql = "INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID,TagFlag) select distinct otherres.netuserid,otherres.resid,otherres.authtype,otherres.resclassid,otherres.tagflag from (select t.netuserid,r.resid,t.authtype," + 
        substr + "(r.restypeid,1,3) resclassid,'TAG' TagFlag " + 
        "from userresauth t,node n,res r,restag tag where t.restypeid='TAG' and t.netuserid='" + userId + "' and n.nodefullcode LIKE '%'||t.nodecode||'%' " + 
        "and (r.nodecodea=n.nodecode or r.nodecodeb=n.nodecode) and r.resid=tag.resid and r.restypeid like '" + resClass + "%' and tag.tag=t.resid) otherres " + 
        "LEFT OUTER JOIN SingleUserResAuth src ON otherres.NetUserId=src.NetUserId AND otherres.ResId=src.ResId AND otherres.AuthType=src.AuthType " + 
        "WHERE src.NetUserId is null AND src.ResId is null AND src.AuthType is null";
      executeUpdate(sql);
    }

    if (updateTime)
    {
      updateUserResAuthTime(userId, "TAG");
    }
    return 0;
  }

  private int executeUpdate(String sql) throws Exception
  {
    int res = 0;
    logHigh("update sql := " + sql);
    Statement stmt = null;
    try
    {
      stmt = this.conn.createStatement();
      res = stmt.executeUpdate(sql);
    }
    finally
    {
      this.oBaseBean.cleanStmt(stmt);
    }
    return res;
  }

  private int executeUpdate(String sql, String[] paras) throws Exception
  {
    int res = 0;

    PreparedStatement prs = null;

    logHigh("update sql := " + sql);

    String v = "";
    try
    {
      prs = this.conn.prepareStatement(sql);
      for (int i = 0; i < paras.length; ++i)
      {
        v = v + "'" + paras[i] + "',";
        prs.setString(i + 1, paras[i]);
      }

      logHigh("prepared values := " + v);

      res = prs.executeUpdate();
    }
    finally
    {
      this.oBaseBean.cleanPre(prs);
    }
    return res;
  }

  private List executeQuery(String sql, int totalCol) throws Exception
  {
    logHigh("query sql := " + sql);

    List result = new ArrayList();

    Statement stmt = null;
    ResultSet rs = null;
    try
    {
      stmt = this.conn.createStatement();
      rs = stmt.executeQuery(sql);
      while (rs.next())
      {
        String[] ss = new String[totalCol];
        for (int i = 0; i < totalCol; ++i)
        {
          ss[i] = rs.getString(i + 1);
        }
        result.add(ss);
      }
    }
    finally
    {
      this.oBaseBean.cleanRs(rs);
      this.oBaseBean.cleanStmt(stmt);
    }

    return result;
  }

  private List getResClass(String resClassId)
    throws Exception
  {
    ArrayList result = new ArrayList();

    String sql = "SELECT ResClassId,ResClassName FROM ResClass WHERE 1=1 ";

    if ((resClassId == null) || (!(resClassId.equalsIgnoreCase("ALL"))))
    {
      sql = sql + " AND ResClassId='" + resClassId + "'";
    }

    List resLs = executeQuery(sql, 2);

    for (int i = 0; i < resLs.size(); ++i)
    {
      String[] ss = (String[])resLs.get(i);
      ResClass rc = new ResClass(null);
      rc.setResClassId(ss[0]);
      rc.setResClassName(ss[1]);
      result.add(rc);
    }
    return result;
  }

  private boolean isUserUpdated(String netUserId)
    throws Exception
  {
    logLow("isUserUpdated is called:" + netUserId);

    UserInfo user = findUserInfo(netUserId);

    String userUpdateDate = user.getAuthUpdateTime();
    String userAuthUpdateTime = null;

    UserAuthUpdate userAuthUpdate = findUserAuthUpdate(netUserId);

    if (userAuthUpdate != null)
    {
      userAuthUpdateTime = userAuthUpdate.getUpdateTime();
    }

    return ((userUpdateDate == null) || 
      (!(userUpdateDate.equals(userAuthUpdateTime))));
  }

  private boolean isNodeUpdated(String netUserId)
    throws Exception
  {
    logLow("isNodeUpdated is called:" + netUserId);

    String nodeUpdateDate = null;
    String userNodeUpdateDate = null;

    UserNodeUpdate userNodeUpdate = findUserNodeUpdate(netUserId);

    if (userNodeUpdate != null)
    {
      nodeUpdateDate = userNodeUpdate.getNodeUpdateTime();
      userNodeUpdateDate = userNodeUpdate.getUpdateTime();
    }

    return ((nodeUpdateDate == null) || 
      (!(nodeUpdateDate.equals(userNodeUpdateDate))));
  }

  private int updateUserNodeAuth(String netUserId, boolean needCheck)
    throws Exception
  {
    logLow("updateUserNodeAuth is called:NetUserId=" + netUserId + 
      ";needcheck=" + needCheck);

    if ((needCheck) && 
      (!(isNodeUpdated(netUserId))))
    {
      logLow("updateUserNodeAuth call end! insert row total is 0");
      return 0;
    }

    int result = 0;

    executeUpdate("DELETE FROM SingleUserNodeAuth WHERE NetUserId='" + 
      netUserId + "'");

    result = result + 
      executeUpdate("INSERT INTO SingleUserNodeAuth(NetUserId,NodeCode,AuthType,NodeFullCode) SELECT DISTINCT b.NetUserId NetUserId,a.NodeCode NodeCode ,'CFG' AuthType,a.nodefullcode NodeFullCode FROM Node a,UserResAuth b WHERE b.AuthType='CFG' AND b.NetUserId='" + 
      netUserId + 
      "' AND b.ResTypeId='ALL' AND a.NodeFullCode LIKE '%'||b.NodeCode||'%'");

    result = result + 
      executeUpdate("INSERT INTO SingleUserNodeAuth(NetUserId,NodeCode,AuthType,NodeFullCode) SELECT DISTINCT b.NetUserId NetUserId,a.NodeCode NodeCode ,'VIEW' AuthType,a.nodefullcode NodeFullCode FROM Node a,UserResAuth b WHERE b.NetUserId='" + 
      netUserId + 
      "' AND b.ResTypeId='ALL' AND (b.AuthType='CFG' OR b.AuthType='VIEW') AND a.NodeFullCode LIKE '%'||b.NodeCode||'%'");

    logLow("updateUserNodeAuth call end!insert row total is " + result);

    updateUserNodeAuthTime(netUserId);

    return result;
  }

  private int updateUserResAuthExtend(String netUserId, List resClassList, boolean needCheck)
    throws Exception
  {
    logLow("updateUserResAuthExtend is called:NetUserId=" + netUserId);

    int result = 0;

    Iterator ite = resClassList.iterator();

    while (ite.hasNext())
    {
      ResClass resClass = (ResClass)ite.next();

      result = result + 
        updateUserResAuth(netUserId, resClass.getResClassId(), 
        needCheck);
    }

    logLow("updateUserResAuthExtend call end! insert row total is " + 
      result);

    return result;
  }

  private boolean isResUpdated(String netUserId, String resClassId)
    throws Exception
  {
    logLow("isResUpdated is called:NetUserId=" + netUserId + ";resclassid=" + 
      resClassId);

    String resUpdateDate = null;
    String userResUpdateDate = null;

    ResUpdate ru = findResUpdate(resClassId);

    if (ru != null)
    {
      resUpdateDate = ru.getUpdateTime();
    }

    UserResUpdate uru = findUserResUpdate(netUserId, resClassId);

    if (uru != null)
    {
      userResUpdateDate = uru.getUpdateTime();
    }

    return ((resUpdateDate == null) || 
      (!(resUpdateDate.equals(userResUpdateDate))));
  }

  private int updateUserResAuth(String netUserId, String resClassId, boolean needCheck)
    throws Exception
  {
    logLow("updateUserResAuth is called:NetUserId=" + netUserId + 
      ";resclassid=" + resClassId + ";needcheck=" + needCheck);

    if ((needCheck) && 
      (!(isResUpdated(netUserId, resClassId))))
    {
      return 0;
    }

    executeUpdate("DELETE FROM SingleUserResAuth WHERE NetUserId='" + 
      netUserId + "' AND ResClassID='" + resClassId + "'");

    int result = 0;

    String distinct = "";
    if (this.resclass2ends.indexOf(resClassId) != -1)
    {
      distinct = "DISTINCT";
    }

    result = result + 
      executeUpdate("INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID) SELECT " + 
      distinct + " '" + 
      netUserId + 
      "' NetUserId,res.ResId,'CFG' AuthType,'" + 
      resClassId + 
      "' ResClassID " + 
      "FROM Res res,SingleUserNodeAuth node " + 
      "WHERE res.ResTypeId LIKE '" + 
      resClassId + 
      "%' AND node.NetUserId='" + 
      netUserId + 
      "' " + 
      "AND node.AuthType='CFG' " + 
      "AND (res.NodeCodeA=node.NodeCode OR res.NodeCodeB=node.NodeCode) ");

    result = result + 
      executeUpdate("INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID) SELECT otherres.NetUserId,otherres.ResId,otherres.AuthType,'" + 
      resClassId + 
      "' ResClassID " + 
      "FROM (SELECT " + distinct + " '" + 
      netUserId + 
      "' NetUserId,res.ResId,'CFG' AuthType " + 
      "FROM Res res,UserResAuth auth " + 
      "WHERE auth.NetUserId='" + 
      netUserId + 
      "' AND auth.AuthType='CFG' " + 
      "AND auth.ResTypeId<>'ALL' AND auth.ResId<>'ALL' " + 
      "AND res.ResId=auth.ResId " + 
      "AND res.ResTypeId LIKE '" + 
      resClassId + 
      "%') otherres LEFT OUTER JOIN SingleUserResAuth src " + 
      "ON otherres.NetUserId=src.NetUserId AND otherres.ResId=src.ResId AND otherres.AuthType=src.AuthType " + 
      "WHERE src.NetUserId is null AND src.ResId is null AND src.AuthType is null");

    this.conn.commit();

    result = result + 
      executeUpdate("INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID) SELECT " + 
      distinct + " '" + 
      netUserId + 
      "' NetUserId,res.ResId,'VIEW' AuthType,'" + 
      resClassId + 
      "' ResClassID " + 
      "FROM Res res,SingleUserNodeAuth node " + 
      "WHERE res.ResTypeId LIKE '" + 
      resClassId + 
      "%' AND node.NetUserId='" + 
      netUserId + 
      "' " + 
      "AND node.AuthType='VIEW' " + 
      "AND (res.NodeCodeA=node.NodeCode OR res.NodeCodeB=node.NodeCode) ");

    result = result + 
      executeUpdate("INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID) SELECT otherres.NetUserId,otherres.ResId,otherres.AuthType,'" + 
      resClassId + 
      "' ResClassID " + 
      "FROM (SELECT " + distinct + " '" + 
      netUserId + 
      "' NetUserId,res.ResId,'VIEW' AuthType " + 
      "FROM Res res,UserResAuth auth " + 
      "WHERE res.ResTypeId LIKE '" + 
      resClassId + 
      "%' AND auth.NetUserId='" + 
      netUserId + 
      "' " + 
      "AND auth.ResTypeId<>'ALL' AND auth.ResId<>'ALL' AND " + 
      "(auth.AuthType='CFG' OR auth.AuthType='VIEW') AND res.ResId=auth.ResId ) " + 
      "otherres LEFT OUTER JOIN SingleUserResAuth src " + 
      "ON otherres.NetUserId=src.NetUserId AND otherres.ResId=src.ResId AND otherres.AuthType=src.AuthType " + 
      "WHERE src.NetUserId is null AND src.ResId is null AND src.AuthType is null");

    result = result + 
      executeUpdate("INSERT INTO SingleUserResAuth (NetUserID,ResID,AuthType,ResClassID) SELECT otherres.NetUserId,otherres.ResId,otherres.AuthType,'" + 
      resClassId + 
      "' ResClassID " + 
      "FROM (SELECT " + distinct + " '" + 
      netUserId + 
      "' NetUserId,a.ResId,'VIEW' AuthType " + 
      "FROM Res a,UserResAuth b,Node c " + 
      "WHERE a.ResTypeId LIKE '" + 
      resClassId + 
      "%' AND b.NetUserId='" + 
      netUserId + 
      "' " + 
      "AND b.ResTypeId<>'ALL' AND b.ResId='ALL' AND b.AuthType='VIEW' " + 
      "AND a.ResTypeId=b.ResTypeId AND (a.NodeCodeA=c.NodeCode OR a.NodeCodeB=c.NodeCode) " + 
      "AND c.nodefullcode LIKE '%'||b.NodeCode||'%' ) " + 
      "otherres LEFT OUTER JOIN SingleUserResAuth src " + 
      "ON otherres.NetUserId=src.NetUserId AND otherres.ResId=src.ResId AND otherres.AuthType=src.AuthType " + 
      "WHERE src.NetUserId is null AND src.ResId is null AND src.AuthType is null");

    this.conn.commit();

    updateUserResAuthTime(netUserId, resClassId);

    this.conn.commit();

    if ((needCheck) && (!(this.tagUpdated)))
    {
      updateUserTagAuth(netUserId, resClassId, false);
    }

    return result;
  }

  private void updateUserAuthTime(String netUserId)
    throws Exception
  {
    String sysDate = getSysdate();

    UserInfo user = findUserInfo(netUserId);

    if (user.getAuthUpdateTime() == null)
    {
      user.setAuthUpdateTime(sysDate);
      updateUserInfo(user);
    }

    UserAuthUpdate userAuthUpdate = findUserAuthUpdate(netUserId);

    if (userAuthUpdate != null)
    {
      userAuthUpdate.setUpdateTime(user.getAuthUpdateTime());
      updateUserAuthUpdate(userAuthUpdate);
    }
    else
    {
      userAuthUpdate = new UserAuthUpdate(null);
      userAuthUpdate.setNetUserId(netUserId);
      userAuthUpdate.setUpdateTime(user.getAuthUpdateTime());
      insertUserAuthUpdate(userAuthUpdate);
    }
  }

  private void updateUserNodeAuthTime(String netUserId)
    throws Exception
  {
    String sysDate = getSysdate();

    UserNodeUpdate userNodeUpdate = findUserNodeUpdate(netUserId);

    if (userNodeUpdate != null)
    {
      if (userNodeUpdate.getNodeUpdateTime() == null)
      {
        userNodeUpdate.setNodeUpdateTime(sysDate);
      }
      userNodeUpdate.setUpdateTime(userNodeUpdate.getNodeUpdateTime());
      updateUserNodeUpdate(userNodeUpdate);
    }
    else
    {
      userNodeUpdate = new UserNodeUpdate(null);
      userNodeUpdate.setNetUserId(netUserId);
      userNodeUpdate.setNodeUpdateTime(sysDate);
      userNodeUpdate.setUpdateTime(sysDate);
      insertUserNodeUpdate(userNodeUpdate);
    }
  }

  private void updateUserResAuthTime(String netUserId, String resClassId)
    throws Exception
  {
    String sysDate = getSysdate();

    ResUpdate resUpdate = null;
    UserResUpdate userResUpdate = null;

    resUpdate = findResUpdate(resClassId);

    if (resUpdate != null)
    {
      if (resUpdate.getUpdateTime() == null)
      {
        resUpdate.setUpdateTime(sysDate);
        updateResUpdate(resUpdate);
      }
    }
    else
    {
      resUpdate = new ResUpdate(null);
      resUpdate.setResClassId(resClassId);
      resUpdate.setUpdateTime(sysDate);
      insertResUpdate(resUpdate);
    }

    userResUpdate = findUserResUpdate(netUserId, resClassId);

    if (userResUpdate != null)
    {
      userResUpdate.setUpdateTime(resUpdate.getUpdateTime());
      updateUserResUpdate(userResUpdate);
    }
    else
    {
      userResUpdate = new UserResUpdate(null);
      userResUpdate.setNetUserId(netUserId);
      userResUpdate.setResClassId(resClassId);
      userResUpdate.setUpdateTime(resUpdate.getUpdateTime());
      insertUserResUpdate(userResUpdate);
    }
  }

  private static List minusList(List fromList, List srcList)
  {
    ArrayList resultList = new ArrayList(fromList.size());

    int from = 0;
    int src = 0;
    do
    {
      SingleUserResAuth fromAuth = (SingleUserResAuth)fromList.get(from);
      SingleUserResAuth srcAuth = (SingleUserResAuth)srcList.get(src);

      int compare = fromAuth.getResId().compareTo(srcAuth.getResId());

      if (compare == 0)
      {
        ++from;
        ++src;
      }
      else if (compare < 0)
      {
        resultList.add(fromAuth);
        ++from;
      }
      else
      {
        ++src;
      }
      if (from >= fromList.size()) break;  }
    while (src < srcList.size());

    while (from < fromList.size())
    {
      resultList.add(fromList.get(from));
      ++from;
    }
    return resultList;
  }

  private UserInfo findUserInfo(String netUserId)
    throws Exception
  {
    logLow("findUserInfo is called:" + netUserId);

    UserInfo userInfo = null;

    String sql = "SELECT NetUserId,AuthUpdateTime FROM UserInfo WHERE NetUserId='" + 
      netUserId + "' ";

    switch (dbType)
    {
    case 0:
      sql = "SELECT NetUserId,TO_CHAR(AuthUpdateTime,'MM-dd-yyyy hh24:mi:ss') AuthUpdateTime FROM UserInfo WHERE NetUserId='" + 
        netUserId + "' ";
      break;
    case 1:
      sql = "SELECT NetUserId,CONVERT(VARCHAR,AuthUpdateTime,110)||' '||CONVERT(VARCHAR,AuthUpdateTime,108) AuthUpdateTime FROM UserInfo WHERE NetUserId='" + 
        netUserId + "' ";
    }

    List userLs = executeQuery(sql, 2);

    if (userLs.size() > 0)
    {
      String[] ss = (String[])userLs.get(0);
      userInfo = new UserInfo(null);
      userInfo.setNetUserId(ss[0]);
      userInfo
        .setAuthUpdateTime(((ss[1] == null) || (ss[1].trim().equals(""))) ? null : 
        ss[1]);
    }

    return userInfo;
  }

  private int updateUserInfo(UserInfo f) throws Exception
  {
    String sql = "UPDATE UserInfo Set AuthUpdateTime=? WHERE NetUserId=?";

    switch (dbType)
    {
    case 0:
      sql = "UPDATE UserInfo Set AuthUpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss') WHERE NetUserId=?";
    case 1:
    }

    return executeUpdate(sql, new String[] { f.getAuthUpdateTime(), 
      f.getNetUserId() });
  }

  private int insertSingleUserResAuth(List l)
    throws Exception
  {
    if ((l != null) && (l.size() != 0))
    {
      int res = 0;
      PreparedStatement prs = null;

      logHigh("update sql := INSERT INTO SingleUserResAuth(NetUserId,ResId,AuthType) VALUES(?,?,?)");
      try
      {
        prs = this.conn
          .prepareStatement("INSERT INTO SingleUserResAuth(NetUserId,ResId,AuthType) VALUES(?,?,?)");

        for (int i = 0; i < l.size(); ++i)
        {
          SingleUserResAuth auth = (SingleUserResAuth)l.get(i);
          logHigh("prepared values := " + auth.getNetUserId() + " " + 
            auth.getResId() + " " + auth.getAuthType());
          prs.setString(1, auth.getNetUserId());
          prs.setString(2, auth.getResId());
          prs.setString(3, auth.getAuthType());
          prs.addBatch();
        }
        int[] oo = prs.executeBatch();
        for (int i = 0; i < oo.length; ++i)
        {
          res += oo[i];
        }
      }
      finally
      {
        this.oBaseBean.cleanPre(prs);
      }
    }
    return 0;
  }

  private int insertSingleUserResAuth(SingleUserResAuth s) throws Exception
  {
    String sql = "INSERT INTO SingleUserResAuth(NetUserId,ResId,AuthType) VALUES(?,?,?)";

    return executeUpdate(sql, new String[] { s.getNetUserId(), 
      s.getResId(), s.getAuthType() });
  }

  private List convertList(List strList)
  {
    ArrayList sl = new ArrayList(256);
    Iterator ite = strList.iterator();
    while (ite.hasNext())
    {
      String[] ss = (String[])ite.next();
      SingleUserResAuth a = new SingleUserResAuth(null);
      a.setNetUserId(ss[0]);
      a.setResId(ss[1]);
      a.setAuthType(ss[2]);
      sl.add(a);
    }

    return sl;
  }

  private UserNodeUpdate findUserNodeUpdate(String netUserId)
    throws Exception
  {
    UserNodeUpdate res = null;

    String sql = "SELECT NetUserId,NodeUpdateTime,UpdateTime FROM UserNodeUpdate WHERE NetUserId='" + 
      netUserId + "' ";

    switch (dbType)
    {
    case 0:
      sql = "SELECT NetUserId,TO_CHAR(NodeUpdateTime,'MM-dd-yyyy hh24:mi:ss') NodeUpdateTime,TO_CHAR(UpdateTime,'MM-dd-yyyy hh24:mi:ss') UpdateTime FROM UserNodeUpdate WHERE NetUserId='" + 
        netUserId + "' ";
      break;
    case 1:
      sql = "SELECT NetUserId,CONVERT(VARCHAR,NodeUpdateTime,110)||' '||CONVERT(VARCHAR,NodeUpdateTime,108) NodeUpdateTime,CONVERT(VARCHAR,UpdateTime,110)||' '||CONVERT(VARCHAR,UpdateTime,108) UpdateTime FROM UserNodeUpdate WHERE NetUserId='" + 
        netUserId + "' ";
    }

    List userLs = executeQuery(sql, 3);

    if (userLs.size() > 0)
    {
      String[] ss = (String[])userLs.get(0);
      res = new UserNodeUpdate(null);
      res.setNetUserId(ss[0]);
      res
        .setNodeUpdateTime(((ss[1] == null) || (ss[1].trim().equals(""))) ? null : 
        ss[1]);
      res.setUpdateTime(((ss[2] == null) || (ss[2].trim().equals(""))) ? null : 
        ss[2]);
    }

    return res;
  }

  private int insertUserNodeUpdate(UserNodeUpdate n) throws Exception
  {
    String sql = "INSERT INTO UserNodeUpdate(NetUserId,NodeUpdateTime,UpdateTime) Values(?,?,?)";

    switch (dbType)
    {
    case 0:
      sql = "INSERT INTO UserNodeUpdate(NetUserId,NodeUpdateTime,UpdateTime) Values(?,TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'),TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'))";
    case 1:
    }

    return executeUpdate(sql, new String[] { n.getNetUserId(), 
      n.getNodeUpdateTime(), n.getUpdateTime() });
  }

  private int updateUserNodeUpdate(UserNodeUpdate u) throws Exception
  {
    String sql = "UPDATE UserNodeUpdate Set NodeUpdateTime=?,UpdateTime=? WHERE NetUserId=?";

    switch (dbType)
    {
    case 0:
      sql = "UPDATE UserNodeUpdate Set NodeUpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'),UpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss') WHERE NetUserId=?";
    case 1:
    }

    return executeUpdate(sql, new String[] { u.getNodeUpdateTime(), 
      u.getUpdateTime(), u.getNetUserId() });
  }

  private UserResUpdate findUserResUpdate(String netUserId, String resClassId)
    throws Exception
  {
    UserResUpdate res = null;

    String sql = "SELECT NetUserId,ResClassId,UpdateTime FROM UserResUpdate WHERE NetUserId='" + 
      netUserId + "' AND ResClassId='" + resClassId + "' ";

    switch (dbType)
    {
    case 0:
      sql = "SELECT NetUserId,ResClassId,TO_CHAR(UpdateTime,'MM-dd-yyyy hh24:mi:ss') UpdateTime FROM UserResUpdate WHERE NetUserId='" + 
        netUserId + "' AND ResClassId='" + resClassId + "' ";
      break;
    case 1:
      sql = "SELECT NetUserId,ResClassId,CONVERT(VARCHAR,UpdateTime,110)||' '||CONVERT(VARCHAR,UpdateTime,108) UpdateTime FROM UserResUpdate WHERE NetUserId='" + 
        netUserId + "' AND ResClassId='" + resClassId + "' ";
    }

    List userLs = executeQuery(sql, 3);

    if (userLs.size() > 0)
    {
      String[] ss = (String[])userLs.get(0);
      res = new UserResUpdate(null);
      res.setNetUserId(ss[0]);
      res.setResClassId(ss[1]);
      res.setUpdateTime(((ss[2] == null) || (ss[2].trim().equals(""))) ? null : 
        ss[2]);
    }

    return res;
  }

  private int insertUserResUpdate(UserResUpdate n) throws Exception
  {
    String sql = "INSERT INTO UserResUpdate(NetUserId,ResClassId,UpdateTime) VALUES(?,?,?)";

    switch (dbType)
    {
    case 0:
      sql = "INSERT INTO UserResUpdate(NetUserId,ResClassId,UpdateTime) VALUES(?,?,TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'))";
    case 1:
    }

    return executeUpdate(sql, new String[] { n.getNetUserId(), 
      n.getResClassId(), n.getUpdateTime() });
  }

  private int updateUserResUpdate(UserResUpdate u) throws Exception
  {
    String sql = "UPDATE UserResUpdate SET UpdateTime=? WHERE NetUserId=? AND ResClassId=?";

    switch (dbType)
    {
    case 0:
      sql = "UPDATE UserResUpdate SET UpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss') WHERE NetUserId=? AND ResClassId=?";
    case 1:
    }

    return executeUpdate(sql, new String[] { u.getUpdateTime(), 
      u.getNetUserId(), u.getResClassId() });
  }

  private ResUpdate findResUpdate(String resId)
    throws Exception
  {
    logLow("findResUpdate is called:" + resId);

    ResUpdate res = null;

    String sql = "SELECT ResClassId,UpdateTime FROM ResUpdate WHERE ResClassId='" + 
      resId + "' ";

    switch (dbType)
    {
    case 0:
      sql = "SELECT ResClassId,TO_CHAR(UpdateTime,'MM-dd-yyyy hh24:mi:ss') UpdateTime FROM ResUpdate WHERE ResClassId='" + 
        resId + "' ";
      break;
    case 1:
      sql = "SELECT ResClassId,CONVERT(VARCHAR,UpdateTime,110)||' '||CONVERT(VARCHAR,UpdateTime,108) UpdateTime FROM ResUpdate WHERE ResClassId='" + 
        resId + "' ";
    }

    List userLs = executeQuery(sql, 2);

    if (userLs.size() > 0)
    {
      String[] ss = (String[])userLs.get(0);
      res = new ResUpdate(null);
      res.setResClassId(ss[0]);
      res.setUpdateTime(((ss[1] == null) || (ss[1].trim().equals(""))) ? null : 
        ss[1]);
    }

    return res;
  }

  private int insertResUpdate(ResUpdate n) throws Exception
  {
    String sql = "INSERT INTO ResUpdate(ResClassId,UpdateTime) VALUES(?,?)";

    switch (dbType)
    {
    case 0:
      sql = "INSERT INTO ResUpdate(ResClassId,UpdateTime) VALUES(?,TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'))";
    case 1:
    }

    return executeUpdate(sql, new String[] { n.getResClassId(), 
      n.getUpdateTime() });
  }

  private int updateResUpdate(ResUpdate u) throws Exception
  {
    String sql = "UPDATE ResUpdate Set UpdateTime=? WHERE ResClassId=?";

    switch (dbType)
    {
    case 0:
      sql = "UPDATE ResUpdate Set UpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss') WHERE ResClassId=?";
    case 1:
    }

    return executeUpdate(sql, new String[] { u.getUpdateTime(), 
      u.getResClassId() });
  }

  private UserAuthUpdate findUserAuthUpdate(String netUserId)
    throws Exception
  {
    UserAuthUpdate res = null;

    String sql = "SELECT NetUserId,UpdateTime FROM UserAuthUpdate WHERE NetUserId='" + 
      netUserId + "' ";

    switch (dbType)
    {
    case 0:
      sql = "SELECT NetUserId,TO_CHAR(UpdateTime,'MM-dd-yyyy hh24:mi:ss') UpdateTime FROM UserAuthUpdate WHERE NetUserId='" + 
        netUserId + "' ";
      break;
    case 1:
      sql = "SELECT NetUserId,CONVERT(VARCHAR,UpdateTime,110)||' '||CONVERT(VARCHAR,UpdateTime,108) UpdateTime FROM UserAuthUpdate WHERE NetUserId='" + 
        netUserId + "' ";
    }

    List userLs = executeQuery(sql, 2);

    if (userLs.size() > 0)
    {
      String[] ss = (String[])userLs.get(0);
      res = new UserAuthUpdate(null);
      res.setNetUserId(ss[0]);
      res.setUpdateTime(((ss[1] == null) || (ss[1].trim().equals(""))) ? null : 
        ss[1]);
    }

    return res;
  }

  private int insertUserAuthUpdate(UserAuthUpdate n) throws Exception
  {
    String sql = "INSERT INTO UserAuthUpdate(NetUserId,UpdateTime) VALUES(?,?)";

    switch (dbType)
    {
    case 0:
      sql = "INSERT INTO UserAuthUpdate(NetUserId,UpdateTime) VALUES(?,TO_DATE(?,'MM-dd-yyyy hh24:mi:ss'))";
    case 1:
    }

    return executeUpdate(sql, new String[] { n.getNetUserId(), 
      n.getUpdateTime() });
  }

  private int updateUserAuthUpdate(UserAuthUpdate u) throws Exception
  {
    String sql = "UPDATE UserAuthUpdate Set UpdateTime=? WHERE NetUserId=?";

    switch (dbType)
    {
    case 0:
      sql = "UPDATE UserAuthUpdate Set UpdateTime=TO_DATE(?,'MM-dd-yyyy hh24:mi:ss') WHERE NetUserId=?";
    case 1:
    }

    return executeUpdate(sql, new String[] { u.getUpdateTime(), 
      u.getNetUserId() });
  }

  private class ResClass
    implements Serializable
  {
    private String resClassId;
    private String resClassName;

    public String getResClassId()
    {
      return this.resClassId;
    }

    public void setResClassId(String resClassId)
    {
      this.resClassId = resClassId;
    }

    public String getResClassName()
    {
      return this.resClassName;
    }

    public void setResClassName(String resClassName)
    {
      this.resClassName = resClassName;
    }
  }

  private class ResUpdate
    implements Serializable
  {
    private String resClassId;
    private String updateTime;

    public String getResClassId()
    {
      return this.resClassId;
    }

    public void setResClassId(String resClassId)
    {
      this.resClassId = resClassId;
    }

    public String getUpdateTime()
    {
      return this.updateTime;
    }

    public void setUpdateTime(String updateTime)
    {
      this.updateTime = updateTime;
    }
  }

  private class SingleUserNodeAuth
    implements Serializable
  {
    private String netUserId;
    private String nodeCode;
    private String authType;
    private String nodeFullCode;

    public String getAuthType()
    {
      return this.authType;
    }

    public void setAuthType(String authType)
    {
      this.authType = authType;
    }

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }

    public String getNodeCode()
    {
      return this.nodeCode;
    }

    public void setNodeCode(String nodeCode)
    {
      this.nodeCode = nodeCode;
    }

    public String getNodeFullCode()
    {
      return this.nodeFullCode;
    }

    public void setNodeFullCode(String nodeFullCode)
    {
      this.nodeFullCode = nodeFullCode;
    }
  }

  private class SingleUserResAuth
    implements Serializable
  {
    private String netUserId;
    private String resId;
    private String authType;

    public String getAuthType()
    {
      return this.authType;
    }

    public void setAuthType(String authType)
    {
      this.authType = authType;
    }

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }

    public String getResId()
    {
      return this.resId;
    }

    public void setResId(String resId)
    {
      this.resId = resId;
    }
  }

  private class UserAuthUpdate
    implements Serializable
  {
    private String netUserId;
    private String updateTime;

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }

    public String getUpdateTime()
    {
      return this.updateTime;
    }

    public void setUpdateTime(String updateTime)
    {
      this.updateTime = updateTime;
    }
  }

  private class UserInfo
    implements Serializable
  {
    private String netUserId;
    private String authUpdateTime;

    public String getAuthUpdateTime()
    {
      return this.authUpdateTime;
    }

    public void setAuthUpdateTime(String authUpdateTime)
    {
      this.authUpdateTime = authUpdateTime;
    }

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }
  }

  private class UserNodeUpdate
    implements Serializable
  {
    private String netUserId;
    private String nodeUpdateTime;
    private String updateTime;

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }

    public String getNodeUpdateTime()
    {
      return this.nodeUpdateTime;
    }

    public void setNodeUpdateTime(String nodeUpdateTime)
    {
      this.nodeUpdateTime = nodeUpdateTime;
    }

    public String getUpdateTime()
    {
      return this.updateTime;
    }

    public void setUpdateTime(String updateTime)
    {
      this.updateTime = updateTime;
    }
  }

  private class UserResUpdate
    implements Serializable
  {
    private String netUserId;
    private String resClassId;
    private String updateTime;

    public String getNetUserId()
    {
      return this.netUserId;
    }

    public void setNetUserId(String netUserId)
    {
      this.netUserId = netUserId;
    }

    public String getResClassId()
    {
      return this.resClassId;
    }

    public void setResClassId(String resClassId)
    {
      this.resClassId = resClassId;
    }

    public String getUpdateTime()
    {
      return this.updateTime;
    }

    public void setUpdateTime(String updateTime)
    {
      this.updateTime = updateTime;
    }
  }

  private class WaitThread extends Thread
  {
    String user;
    String resclass;
    Connection con;

    public WaitThread(String paramString1, String paramString2, Connection paramConnection)
    {
      this.user = paramString1;
      this.resclass = paramString2;
      this.con = paramConnection;
    }

    public void run()
    {
      try
      {
        UserAuthManager.getInstance().updateUserAuth2(this.user, this.resclass, this.con);
      }
      catch (Exception localException)
      {
      }
    }
  }
}