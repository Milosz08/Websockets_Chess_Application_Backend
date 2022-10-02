<#import "macro/common.macro.ftl" as m>
<@m.commonEmailWrapper>
    <table style="font-family:arial,helvetica,sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0"><tbody><tr>
        <td style="overflow-wrap:break-word;word-break:break-word;padding:20px 0px 0px;font-family:arial,helvetica,sans-serif;" align="left">
            <div style="line-height: 140%; text-align: left; word-wrap: break-word;">
                <p style="font-size: 14px; line-height: 140%;"><span style="font-size: 14px; line-height: 19.6px; color: #8e8c8c; font-family: Rubik, sans-serif;">Dear <span style="color: #3b1302">${userName}</span>!</span></p>
                <p style="font-size: 14px; line-height: 140%;"> </p>
                <p style="font-size: 14px; line-height: 140%;"><span style="font-size: 14px; line-height: 19.6px; color: #8e8c8c; font-family: Rubik, sans-serif;">You have received this message because you have sent a request to activate your account. If you still wish to activate your account, please rewrite the code below:</span></p>
            </div>
        </td>
    </tr></tbody></table>
    <#include "fragment/ota-token-with-bearer-button.fragment.ftl">
</@m.commonEmailWrapper>
