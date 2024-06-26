. "$PSScriptRoot\..\..\common\builder\builder.ps1"

$classPath = "$PSScriptRoot\gradle\wrapper\gradle-wrapper.jar"
$appName = Split-Path -Path ($PSScriptRoot) -Leaf
$gradle = "$(Find-Java)\bin\java.exe ""-Dorg.gradle.appname=$appName"" -classpath ""$classPath"" org.gradle.wrapper.GradleWrapperMain $args"

Write-Host "Invoking $gradle" -ForegroundColor DarkGray
Invoke-Expression $gradle