package cn.wemalls.manage.buyer.action;

import cn.wemalls.core.annotation.SecurityMapping;
import cn.wemalls.core.domain.virtual.SysMap;
import cn.wemalls.core.mv.JModelAndView;
import cn.wemalls.core.query.support.IPageList;
import cn.wemalls.core.security.support.SecurityUserHolder;
import cn.wemalls.core.tools.CommUtil;
import cn.wemalls.foundation.domain.query.ConsultQueryObject;
import cn.wemalls.foundation.service.IConsultService;
import cn.wemalls.foundation.service.ISysConfigService;
import cn.wemalls.foundation.service.IUserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 买家咨询控制器
 */
@Controller
public class ConsultBuyerAction {
    @Autowired
    private ISysConfigService configService;

    @Autowired
    private IUserConfigService userConfigService;

    @Autowired
    private IConsultService consultService;

    @SecurityMapping(display = false, rsequence = 0, title = "买家咨询列表", value = "/buyer/consult.htm*", rtype = "buyer", rname = "用户中心", rcode = "user_center", rgroup = "用户中心")
    @RequestMapping({"/buyer/consult.htm"})
    public ModelAndView consult(HttpServletRequest request, HttpServletResponse response, String reply, String currentPage){
        ModelAndView mv = new JModelAndView(
            "user/default/usercenter/buyer_consult.html", this.configService
            .getSysConfig(),
            this.userConfigService.getUserConfig(), 0, request, response);
        ConsultQueryObject qo = new ConsultQueryObject(currentPage, mv,
                "addTime", "desc");
        if (!CommUtil.null2String(reply).equals("")){
            qo.addQuery("obj.reply",
                        new SysMap("reply",
                                   Boolean.valueOf(CommUtil.null2Boolean(reply))), "=");
        }
        qo.addQuery("obj.consult_user.id",
                    new SysMap("consult_user",
                               SecurityUserHolder.getCurrentUser().getId()), "=");
        IPageList pList = this.consultService.list(qo);
        CommUtil.saveIPageList2ModelAndView("", "", "", pList, mv);
        mv.addObject("reply", CommUtil.null2String(reply));

        return mv;
    }
}




