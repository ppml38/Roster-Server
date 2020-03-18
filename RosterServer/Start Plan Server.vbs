Set oShell = CreateObject ("Wscript.Shell") 
Dim strArgs
strArgs = "cmd /c java PlanServer"
oShell.Run strArgs, 0, false