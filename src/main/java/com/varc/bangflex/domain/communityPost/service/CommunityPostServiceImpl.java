package com.varc.bangflex.domain.communityPost.service;

import com.varc.bangflex.domain.communityPost.dto.CommunityLikeCreateDTO;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostCreateDTO;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostDTO;
import com.varc.bangflex.domain.communityPost.dto.CommunityPostUpdateDTO;
import com.varc.bangflex.domain.communityPost.entity.CommunityFile;
import com.varc.bangflex.domain.communityPost.entity.CommunityLike;
import com.varc.bangflex.domain.communityPost.repository.CommunityFileRepository;
import com.varc.bangflex.domain.communityPost.repository.CommunityLikeRepository;
import com.varc.bangflex.domain.communityPost.repository.CommunityPostRepository;
import com.varc.bangflex.domain.communityPost.entity.CommunityPost;
import com.varc.bangflex.domain.user.entity.Member;
import com.varc.bangflex.domain.user.repository.UserRepository;
import com.varc.bangflex.exception.InvalidUserException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service("communityPostService")
public class CommunityPostServiceImpl implements CommunityPostService {

    private final ModelMapper modelMapper;
    private final CommunityPostRepository communityPostRepository;
    private final UserRepository userRepository;
    private final CommunityFileRepository communityFileRepository;
    private final CommunityLikeRepository communityLikeRepository;

    @Autowired
    public CommunityPostServiceImpl(ModelMapper modelMapper,
                                    CommunityPostRepository communityPostRepository,
                                    UserRepository userRepository,
                                    CommunityFileRepository communityFileRepository, CommunityLikeRepository communityLikeRepository) {
        this.modelMapper = modelMapper;
        this.communityPostRepository = communityPostRepository;
        this.userRepository = userRepository;
        this.communityFileRepository = communityFileRepository;
        this.communityLikeRepository = communityLikeRepository;
    }

    @Transactional
    @Override
    public void createPost(String loginId, CommunityPostCreateDTO newPost, List<MultipartFile> images) throws IOException {
        CommunityPost createdPost = modelMapper.map(newPost, CommunityPost.class);

        // 회원이 아니라면 예외 발생
        Member member = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("회원가입이 필요합니다."));

        createdPost.setTitle(newPost.getTitle());
        createdPost.setContent(newPost.getContent());
        createdPost.setCreatedAt(LocalDateTime.now());
        createdPost.setActive(true);
        createdPost.setMember(member);

        // 게시글 저장
        communityPostRepository.save(createdPost);

        // 게시글 첨부파일 있으면 저장
        if (images != null) {
            List<CommunityFile> addedImages = saveFiles(images, createdPost);
            createdPost.setCommunityFiles(addedImages);
        }
    }

    private List<CommunityFile> saveFiles(List<MultipartFile> images, CommunityPost savedPost) throws IOException {
        List<CommunityFile> communityFiles = new ArrayList<>();

        for (MultipartFile file : images) {
            String fileName = file.getOriginalFilename();

            // 파일이름만 남김
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
            // UUID 생성
            String uuid = UUID.randomUUID().toString();
            // 저장 경로
            String filePath = "src/main/resources/static/uploadFiles/communityFiles/" + uuid + fileName;
            Path path = Paths.get(filePath);
            // DB 저장명
            String dbUrl = "/uploadFiles/communityFiles/" + uuid + fileName;

            //저장
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());

            CommunityFile addedImages = communityFileRepository.save(CommunityFile.builder()
                    .url(dbUrl)
                    .createdAt(LocalDateTime.now())
                    .active(true)
                    .communityPost(savedPost)
                    .build()
            );

            communityFiles.add(addedImages);
        }

        return communityFiles;
    }

    @Transactional
    @Override
    public void updatePost(String loginId, int communityPostCode, CommunityPostUpdateDTO modifiedPost, List<MultipartFile> images) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                                    .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("로그인이 필요합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundPost.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException("게시글 수정 권한이 없습니다.");
        }

        foundPost.setTitle(modifiedPost.getTitle());
        foundPost.setContent(modifiedPost.getContent());

        // 수정된 게시글 저장
        communityPostRepository.save(foundPost);
    }

    @Transactional
    @Override
    public void deletePost(String loginId, int communityPostCode) {
        CommunityPost foundPost = communityPostRepository.findById(communityPostCode)
                                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        // 회원이 아니라면 예외 발생
        Member author = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("로그인이 필요합니다."));

        // 게시글 작성자가 아니라면 예외 발생
        if (!foundPost.getMember().getMemberCode().equals(author.getMemberCode())) {
            throw new InvalidUserException("게시글 삭제 권한이 없습니다.");
        }

        foundPost.setActive(false);

        communityPostRepository.save(foundPost);
    }

//    @Transactional(readOnly = true)
//    @Override
//    public Page<CommunityPostDTO> findPostList(Pageable pageable) {
//        pageable = PageRequest.of(pageable.getPageNumber() <= 0 ? 0 : pageable.getPageNumber() - 1,
//                pageable.getPageSize(),
//                Sort.by("communityPostCode").descending());
//
//        Page<CommunityPost> postList = communityPostRepository.findByActiveTrue(pageable);
//
//        List<CommunityPostDTO> posts = postList.getContent().stream()
//                .map(post -> {
//                    CommunityPostDTO dto = modelMapper.map(post, CommunityPostDTO.class);
//                    dto.setMemberCode(post.getMember().getMemberCode());
//                    return dto;
//                })
//                .toList();
//
//        return new PageImpl<>(posts, pageable, postList.getTotalElements());
//    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityPostDTO> getAllPosts(String loginId) {
        List<CommunityPost> allPosts = communityPostRepository.findByActiveTrue(
                                                                Sort.by("createdAt").descending());

        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(() -> new InvalidUserException("회원가입이 필요합니다."));

        List<CommunityPostDTO> postList = allPosts.stream()
                .map(communityPost -> {
                    CommunityPostDTO postDTO = modelMapper.map(communityPost, CommunityPostDTO.class);

                    List<CommunityFile> images = communityFileRepository.findByCommunityPost(communityPost);

                    List<String> urls = images.stream().map(CommunityFile::getUrl).toList();

                    boolean isLike = communityLikeRepository
                            .existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                                    loginMember.getMemberCode(), communityPost.getCommunityPostCode());

                    int likeCount = communityLikeRepository
                            .countByCommunityPostCodeAndActiveTrue(communityPost.getCommunityPostCode());

                    postDTO.setNickname(communityPost.getMember().getNickname());
                    postDTO.setProfile(communityPost.getMember().getImage());
                    postDTO.setImageUrls(urls);
                    postDTO.setLiked(isLike);
                    postDTO.setLikeCount(likeCount);

                    return postDTO;
                }).toList();

        return postList;
    }

    @Transactional(readOnly = true)
    @Override
    public CommunityPostDTO findPostByCode(String loginId, int communityPostCode) {
        CommunityPost post = communityPostRepository.findById(communityPostCode)
                            .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(() -> new InvalidUserException("회원가입이 필요합니다."));

        CommunityPostDTO selectedPost = modelMapper.map(post, CommunityPostDTO.class);
        selectedPost.setNickname(post.getMember().getNickname());
        selectedPost.setProfile(post.getMember().getImage());

        List<CommunityFile> images = communityFileRepository.findByCommunityPost(post);

        List<String> urls = images.stream().map(CommunityFile::getUrl).toList();

        boolean isLike = communityLikeRepository
                .existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                        loginMember.getMemberCode(), post.getCommunityPostCode());

        int likeCount = communityLikeRepository
                .countByCommunityPostCodeAndActiveTrue(post.getCommunityPostCode());

        selectedPost.setImageUrls(urls);
        selectedPost.setLiked(isLike);
        selectedPost.setLikeCount(likeCount);

        return selectedPost;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommunityPostDTO> getMyPosts(String loginId) {
        Member loginMember = userRepository.findById(loginId)
                .orElseThrow(() -> new InvalidUserException("회원가입이 필요합니다."));

        List<CommunityPost> myPosts = communityPostRepository
                .findByMemberAndActiveTrueOrderByCreatedAtDesc(loginMember);

        List<CommunityPostDTO> myPostList = myPosts.stream()
                .map(communityPost -> {
                    CommunityPostDTO postDTO = modelMapper.map(communityPost, CommunityPostDTO.class);

                    List<CommunityFile> images = communityFileRepository.findByCommunityPost(communityPost);

                    List<String> urls = images.stream().map(CommunityFile::getUrl).toList();

                    boolean isLike = communityLikeRepository
                            .existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                                    loginMember.getMemberCode(), communityPost.getCommunityPostCode());

                    int likeCount = communityLikeRepository
                            .countByCommunityPostCodeAndActiveTrue(communityPost.getCommunityPostCode());

                    postDTO.setNickname(communityPost.getMember().getNickname());
                    postDTO.setProfile(communityPost.getMember().getImage());
                    postDTO.setImageUrls(urls);
                    postDTO.setLiked(isLike);
                    postDTO.setLikeCount(likeCount);

                    return postDTO;
                }).toList();

        return myPostList;
    }

    @Transactional
    @Override
    public void addLike(String loginId, CommunityLikeCreateDTO newLike) {
        CommunityLike addedLike = modelMapper.map(newLike, CommunityLike.class);

        // 회원이 아니라면 예외 발생
        Member likeMember = userRepository.findById(loginId).orElseThrow(
                () -> new InvalidUserException("회원가입이 필요합니다."));

        CommunityPost likePost = communityPostRepository.findById(newLike.getCommunityPostCode())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 게시글입니다."));

        addedLike.setMemberCode(likeMember.getMemberCode());
        addedLike.setCommunityPostCode(likePost.getCommunityPostCode());
        addedLike.setCreatedAt(LocalDateTime.now());

        // 이미 좋아요가 존재하는지 체크 후 존재하면 좋아요 취소(비활성화)
        if (communityLikeRepository.existsByMemberCodeAndCommunityPostCodeAndActiveTrue(
                likeMember.getMemberCode(),
                likePost.getCommunityPostCode())) {
            addedLike.setActive(false);
        } else {
            addedLike.setActive(true);
        }

        communityLikeRepository.save(addedLike);
    }
}
