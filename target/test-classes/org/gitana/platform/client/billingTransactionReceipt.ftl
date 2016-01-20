This is the test billing transaction receipt.
<br/>
<br/>
Tranasction
<br/>
<ul>
    <li>ID: ${transaction.id}</li>
    <li>Amount: ${transaction.amount!}</li>
    <#if transaction.taxAmount?exists>
        <li>Tax Amount: ${transaction.taxAmount}</li>
    </#if>
    <#if transaction.planKey?exists>
        <li>Plan Key: ${transaction.planKey}</li>
    </#if>
    <li>Currency Code: ${transaction.currencyIsoCode!}</li>
    <li>Created On: ${transaction.createdOn?date}</li>
    <li>Payment Method Number (masked): ${transaction.paymentMethodNumberMasked}</li>
    <li>Payment Method Type: ${transaction.paymentMethodType}</li>
    <li>Tenant: ${tenant.title!""}</li>
    <li>Owner Email: ${owner.email!""}</li>
</ul>
