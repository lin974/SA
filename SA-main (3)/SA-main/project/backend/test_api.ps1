
$postUrl = "http://localhost:8080/api/posts"
$headers = @{ "Content-Type" = "application/json"; "Authorization" = "Bearer fake-token-admin@gmail.com" }
$body = '{"title":"TestTitle","content":"TestContent"}'

try {
    # 1. Create Post
    $response = Invoke-RestMethod -Uri $postUrl -Method Post -Headers $headers -Body $body
    Write-Host "Create Response: $($response.message)"

    # 2. Get List to find ID
    $list = Invoke-RestMethod -Uri $postUrl -Method Get
    $id = $list[0].id
    Write-Host "Latest Post ID: $id"

    # 3. Get Detail
    $detail = Invoke-RestMethod -Uri "$postUrl/$id" -Method Get
    Write-Host "Detail Title: $($detail.title)"
    Write-Host "Detail Content: $($detail.content)"
    
    if ([string]::IsNullOrWhiteSpace($detail.content)) {
        Write-Host "ERROR: Content is empty!"
    } else {
        Write-Host "SUCCESS: Content is present."
    }
} catch {
    Write-Host "Error: $_"
}
