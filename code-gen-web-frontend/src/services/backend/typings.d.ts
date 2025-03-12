declare namespace API {
  type AddGeneratorCommentRequest = {
    content?: string;
    generatorId?: number;
    parentId?: number;
    userId?: number;
  };

  type AddOrderRequest = {
    amount?: number;
    generatorId?: number;
    userId?: number;
  };

  type BaseResponseBoolean_ = {
    code?: number;
    data?: boolean;
    message?: string;
  };

  type BaseResponseGeneratorVO_ = {
    code?: number;
    data?: GeneratorVO;
    message?: string;
  };

  type BaseResponseLoginUserVO_ = {
    code?: number;
    data?: LoginUserVO;
    message?: string;
  };

  type BaseResponseLong_ = {
    code?: number;
    data?: number;
    message?: string;
  };

  type BaseResponseMapStringObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseObject_ = {
    code?: number;
    data?: Record<string, any>;
    message?: string;
  };

  type BaseResponseOrderVO_ = {
    code?: number;
    data?: OrderVO;
    message?: string;
  };

  type BaseResponsePageGenerator_ = {
    code?: number;
    data?: PageGenerator_;
    message?: string;
  };

  type BaseResponsePageGeneratorCommentVO_ = {
    code?: number;
    data?: PageGeneratorCommentVO_;
    message?: string;
  };

  type BaseResponsePageGeneratorVO_ = {
    code?: number;
    data?: PageGeneratorVO_;
    message?: string;
  };

  type BaseResponsePageUser_ = {
    code?: number;
    data?: PageUser_;
    message?: string;
  };

  type BaseResponsePageUserVO_ = {
    code?: number;
    data?: PageUserVO_;
    message?: string;
  };

  type BaseResponsePayResponse_ = {
    code?: number;
    data?: PayResponse;
    message?: string;
  };

  type BaseResponseString_ = {
    code?: number;
    data?: string;
    message?: string;
  };

  type BaseResponseUser_ = {
    code?: number;
    data?: User;
    message?: string;
  };

  type BaseResponseUserGenerator_ = {
    code?: number;
    data?: UserGenerator;
    message?: string;
  };

  type BaseResponseUserVO_ = {
    code?: number;
    data?: UserVO;
    message?: string;
  };

  type ChangePasswordRequest = {
    newPassword?: string;
    oldPassword?: string;
  };

  type deleteCommentUsingDELETEParams = {
    /** id */
    id: number;
  };

  type DeleteRequest = {
    id?: number;
  };

  type FileConfig = {
    files?: FileInfo[];
    generatedProjectPath?: string;
    generatorPath?: string;
    originProjectPath?: string;
    type?: string;
  };

  type fileDownloadUsingGETParams = {
    /** filePath */
    filePath?: string;
  };

  type FileInfo = {
    condition?: string;
    files?: FileInfo[];
    generateType?: string;
    groupKey?: string;
    groupName?: string;
    inputPath?: string;
    outputPath?: string;
    type?: string;
  };

  type findPasswordUsingGETParams = {
    /** email */
    email?: string;
  };

  type Generator = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    fileConfig?: string;
    id?: number;
    isDelete?: number;
    modelConfig?: string;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string;
    updateTime?: string;
    userId?: number;
    version?: string;
  };

  type GeneratorAddRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    generatorFee?: GeneratorFeeVO;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    userId?: number;
    version?: string;
  };

  type GeneratorCommentVO = {
    content?: string;
    createTime?: string;
    generatorId?: number;
    id?: number;
    likeCount?: number;
    parentId?: number;
    status?: number;
    updateTime?: string;
    userVO?: UserVO;
  };

  type GeneratorEditRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    tags?: string[];
    version?: string;
  };

  type GeneratorFeeVO = {
    createTime?: string;
    isFree?: number;
    price?: number;
    validity?: string;
  };

  type GeneratorQueryRequest = {
    author?: string;
    basePackage?: string;
    current?: number;
    description?: string;
    id?: number;
    name?: string;
    orTags?: string[];
    pageSize?: number;
    searchText?: string;
    sortField?: string;
    sortOrder?: string;
    status?: number;
    tags?: string[];
    userId?: number;
  };

  type GeneratorUpdateRequest = {
    author?: string;
    basePackage?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    version?: string;
  };

  type GeneratorVO = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    distPath?: string;
    fileConfig?: FileConfig;
    generatorFee?: GeneratorFeeVO;
    id?: number;
    modelConfig?: ModelConfig;
    name?: string;
    picture?: string;
    status?: number;
    tags?: string[];
    updateTime?: string;
    user?: UserVO;
    userId?: number;
    version?: string;
  };

  type getDownloadTrendUsingGETParams = {
    /** end */
    end?: string;
    /** start */
    start?: string;
  };

  type getGeneratorForUserPurchaseUsingGETParams = {
    /** generatorId */
    generatorId?: number;
    /** userId */
    userId?: number;
  };

  type getGeneratorRankingUsingGETParams = {
    /** count */
    count?: number;
  };

  type getGeneratorVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getNewUserTrendUsingGETParams = {
    /** end */
    end?: string;
    /** start */
    start?: string;
  };

  type getOrderByIdUsingGETParams = {
    /** orderId */
    orderId: string;
  };

  type getUserByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type getUserVOByIdUsingGETParams = {
    /** id */
    id?: number;
  };

  type isFreeByIdUsingGETParams = {
    /** id */
    id: number;
  };

  type LoginUserVO = {
    createTime?: string;
    id?: number;
    updateTime?: string;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type MakingGeneratorRequest = {
    meta?: Meta;
    zipTemplateFilesUrl?: string;
  };

  type Meta = {
    author?: string;
    basePackage?: string;
    createTime?: string;
    description?: string;
    fileConfig?: FileConfig;
    modelConfig?: ModelConfig;
    name?: string;
    version?: string;
  };

  type ModelConfig = {
    models?: ModelInfo[];
  };

  type ModelInfo = {
    abbr?: string;
    condition?: string;
    defaultValue?: Record<string, any>;
    description?: string;
    fieldName?: string;
    groupArgsStr?: string;
    groupKey?: string;
    groupName?: string;
    models?: ModelInfo[];
    type?: string;
  };

  type OrderItem = {
    asc?: boolean;
    column?: string;
  };

  type OrderVO = {
    amount?: number;
    createTime?: string;
    expireTime?: string;
    generator?: GeneratorVO;
    orderId?: string;
    orderStatus?: number;
    outTradeNo?: string;
    payTime?: string;
    paymentMethod?: string;
    paymentNo?: string;
    remark?: string;
    user?: UserVO;
  };

  type PageGenerator_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: Generator[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageGeneratorCommentVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: GeneratorCommentVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageGeneratorVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: GeneratorVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type pageListCommentsUsingGETParams = {
    generatorId?: number;
    pageNum?: number;
    pageSize?: number;
  };

  type PageUser_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: User[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PageUserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: OrderItem[];
    pages?: number;
    records?: UserVO[];
    searchCount?: boolean;
    size?: number;
    total?: number;
  };

  type PayRequest = {
    orderId?: string;
    subject?: string;
  };

  type PayResponse = {
    htmlPage?: string;
  };

  type ReplyGeneratorCommentRequest = {
    content?: string;
    generatorId?: number;
    parentId?: number;
    userId?: number;
  };

  type ResetPasswordRequest = {
    email?: string;
    newPassword?: string;
    token?: string;
  };

  type uploadFileUsingPOSTParams = {
    biz?: string;
  };

  type UseGeneratorRequest = {
    dataModel?: Record<string, any>;
    id?: number;
  };

  type User = {
    admin?: boolean;
    createTime?: string;
    id?: number;
    isDelete?: number;
    updateTime?: string;
    userAccount?: string;
    userAvatar?: string;
    userEmail?: string;
    userName?: string;
    userPassword?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserAddRequest = {
    userAccount?: string;
    userAvatar?: string;
    userName?: string;
    userRole?: string;
  };

  type UserGenerator = {
    expireTime?: string;
    generatorId?: number;
    purchaseTime?: string;
    status?: number;
    userId?: number;
  };

  type UserLoginRequest = {
    userAccount?: string;
    userPassword?: string;
  };

  type UserQueryRequest = {
    current?: number;
    id?: number;
    pageSize?: number;
    sortField?: string;
    sortOrder?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserRegisterRequest = {
    checkPassword?: string;
    userAccount?: string;
    userPassword?: string;
  };

  type UserUpdateMyRequest = {
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
  };

  type UserUpdateRequest = {
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };

  type UserVO = {
    createTime?: string;
    id?: number;
    userAvatar?: string;
    userName?: string;
    userProfile?: string;
    userRole?: string;
  };
}
