package com.gameplatform.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.IOException;
import software.amazon.awssdk.core.signer.Presigner;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ServiceClientConfiguration;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectAclRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class R2Service {
	private final S3Client s3Client;
	private final S3Presigner s3Presigner;
	@Value("${cloudflare.r2.bucket}")
	private String bucket;
	
	@Value("${cloudflare.r2.public-url}")
	private String publicUrl; // Add this to your application.properties
	public R2Service(S3Client s3Client, S3Presigner s3Presigner)
	{
		this.s3Client = s3Client;
		this.s3Presigner = s3Presigner;
	}
	public String generatePresignedPutUrl(String key, Duration expiry)
	{
		PutObjectRequest request = PutObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		PutObjectPresignRequest presignedRequest = PutObjectPresignRequest.builder()
				.signatureDuration(expiry)
				.putObjectRequest(request)
				.build();
		return s3Presigner.presignPutObject(presignedRequest)
				.url()
				.toString();
	}
	public InputStream download(String key)
	{
		GetObjectRequest request = GetObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build();
		return s3Client.getObject(request);
	}
	public boolean exists(String key)
	{
		try
		{
			s3Client.headObject(
					HeadObjectRequest.builder()
					.bucket(bucket)
					.key(key)
					.build()
					);
			return true;
		} catch (NoSuchKeyException e) {
			return false;
		}
	}
	public void delete(String key)
	{
		s3Client.deleteObject(
				DeleteObjectRequest.builder()
				.bucket(bucket)
				.key(key)
				.build()
				);
	}
	public void deleteFolder(String prefix)
	{
		String continuationToken = null;
		do {
			ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
					.bucket(bucket)
					.prefix(prefix)
					.continuationToken(continuationToken)
					.build();
			ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
			if(!listResponse.contents().isEmpty())
			{
				List<ObjectIdentifier> objectsToDelete = listResponse.contents()
						.stream().map(obj->ObjectIdentifier.builder().key(obj.key()).build())
						.collect(Collectors.toList());
				
				DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
	                    .bucket(bucket)
	                    .delete(Delete.builder().objects(objectsToDelete).build())
	                    .build();
				s3Client.deleteObjects(deleteRequest);
			}
		}while(continuationToken !=null);
	}
	public void unzipAndUpload(String zipKey, String targetFolder) throws IOException
	{
		InputStream zipStream = download(zipKey);
		try(ZipInputStream zis = new ZipInputStream(zipStream))
		{
			ZipEntry entry;
			while((entry = zis.getNextEntry())!=null)
			{
				if(!entry.isDirectory())
				{
					String fileName = entry.getName();
					String key = targetFolder+fileName;
					
					//Read the file content into a byte array
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer  = new byte[1024];
					int len;
					while((len = zis.read(buffer))>0)
					{
						baos.write(buffer,0, len);
					}
					byte[] fileContent = baos.toByteArray();
					//upload to R2
					s3Client.putObject(
							PutObjectRequest.builder()
							.bucket(bucket)
							.key(key)
							.contentType(getContentType(fileName))
							.build(),
							RequestBody.fromBytes(fileContent)
							);
				}
				zis.closeEntry();
			}
		}
	}
	public String getPublicUrl(String key) 
	{
		return publicUrl+"/"+key;
	}
	private String getContentType(String fileName)
	{
		String lowerFileName = fileName.toLowerCase();
		if(lowerFileName.endsWith(".html")) return "text/html";
		if(lowerFileName.endsWith(".js")) return "application/javascript";
		if(lowerFileName.endsWith(".css")) return "text/css";
		if(lowerFileName.endsWith(".png")) return "image/png";
		if(lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) return "image/jpeg";
		if(lowerFileName.endsWith(",gif")) return "image/gif";
		if(lowerFileName.endsWith("svg")) return "image/svg+xml";
		if(lowerFileName.endsWith(".json")) return "application/json";
		if(lowerFileName.endsWith(".wasm")) return "application/wasm";
		if(lowerFileName.endsWith(".mp3")) return "audio/mpeg";
		if(lowerFileName.endsWith(".wav")) return "audio/wav";
		if(lowerFileName.endsWith(".ogg")) return "audio/ogg";
		
		return "application/octet-stream";
		
		
	}
}
