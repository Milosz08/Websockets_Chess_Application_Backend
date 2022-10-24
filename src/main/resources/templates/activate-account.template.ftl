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
    <#include "fragment/ota-token.fragment.ftl">
    <table style="font-family:arial,helvetica,sans-serif;" role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0"><tbody><tr>
        <td style="overflow-wrap:break-word;word-break:break-word;padding:40px 0px;font-family:arial,helvetica,sans-serif;" align="left">
            <div align="center">
                <!--[if mso]><table width="100%" cellpadding="0" cellspacing="0" border="0" style="border-spacing: 0; border-collapse: collapse; mso-table-lspace:0pt; mso-table-rspace:0pt;font-family:arial,helvetica,sans-serif;"><tr><td style="font-family:arial,helvetica,sans-serif;" align="center"><v:roundrect xmlns:v="urn:schemas-microsoft-com:vml" xmlns:w="urn:schemas-microsoft-com:office:word" href="" style="height:39px; v-text-anchor:middle; width:217px;" arcsize="15.5%" stroke="f" fillcolor="#3b1302"><w:anchorlock/><center style="color:#FFFFFF;font-family:arial,helvetica,sans-serif;"><![endif]-->
                <a href="${bearerButtonLink}" target="_blank" style="box-sizing: border-box;display: inline-block;font-family:arial,helvetica,sans-serif;text-decoration: none;-webkit-text-size-adjust: none;text-align: center;color: #FFFFFF; background-color: #3b1302; border-radius: 6px 0 6px 0;-webkit-border-radius: 6px 0 6px 0; -moz-border-radius: 6px 0 6px 0; width:auto; max-width:100%; overflow-wrap: break-word; word-break: break-word; word-wrap:break-word; mso-border-alt: none;">
                    <span style="display:block;padding:10px 20px;line-height:120%;"><p style="font-size: 14px; line-height: 120%;"><span style="font-size: 16px; line-height: 19.2px; font-family: Rubik, sans-serif;">Activate account</span></p></span>
                </a>
                <!--[if mso]></center></v:roundrect></td></tr></table><![endif]-->
            </div>
        </td>
    </tr></tbody></table>
</@m.commonEmailWrapper>
