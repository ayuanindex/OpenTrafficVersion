package com.realmax.opentrafficversion.dao;

import com.j256.ormlite.dao.Dao;
import com.realmax.opentrafficversion.bean.ViolateBean;

import java.sql.SQLException;
import java.util.List;

public class ViolateDaoUtil {
    private static OrmHelper ormHelper;
    private static Dao<ViolateBean, ?> violateDao = null;

    public static Dao<ViolateBean, ?> getDao() {
        try {
            ormHelper = OrmHelper.getInstance();
            violateDao = ormHelper.getDao(ViolateBean.class);
            return violateDao;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return violateDao;
    }

    /**
     * 对一个对象进行更新操作
     *
     * @param violateBean
     */
    public static void add(ViolateBean violateBean) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (getDao() != null) {
                        violateDao.create(violateBean);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 更新指定车辆的违章次数
     */
    public static void updateToCount(ViolateBean violateBean) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (getDao() != null) {
                        List<ViolateBean> violateBeans = queryByNumberPlate(violateBean);
                        if (violateBeans.size() >= 1) {
                            ViolateBean bean = violateBeans.get(0);
                            bean.setViolate(false);
                            bean.setCamera_two(violateBean.getCamera_two());
                            bean.setViolateCount(violateBean.getViolateCount());
                            violateDao.update(bean);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 更新第一次拍照的摄像头
     *
     * @param violateBean
     */
    public static void updateToCamera(ViolateBean violateBean) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (getDao() != null) {
                        List<ViolateBean> violateBeans = queryByNumberPlate(violateBean);
                        if (violateBeans.size() >= 1) {
                            ViolateBean bean = violateBeans.get(0);
                            bean.setCamera(violateBean.getCamera());
                            bean.setViolate(true);
                            violateDao.update(bean);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 找出传入的对象是否存在于数据库
     *
     * @param violateBean 需要查找的对象
     * @return
     * @throws SQLException
     */
    private static List<ViolateBean> queryByNumberPlate(ViolateBean violateBean) throws SQLException {
        List<ViolateBean> numberPlate = violateDao.queryBuilder().where().eq("numberPlate", violateBean.getNumberPlate()).query();
        return numberPlate;
    }
}
